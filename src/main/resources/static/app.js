
const $ = (selector) => document.querySelector(selector);
const $$ = (selector) => [...document.querySelectorAll(selector)];

const API = {
  base: "", // Mesmo servidor Spring Boot
  login: "/auth/login",
  register: "/usuarios",

  // Preencha somente depois de conferir seus Controllers:
  books: null,
  loans: null,
  returns: null,
  history: null
};

const demoBooks = [
  {
    id: "demo-1",
    titulo: "It: A Coisa",
    autor: "Stephen King",
    anoPublicacao: 1986,
    paginas: 1104,
    disponivel: true,
    capa: "https://covers.openlibrary.org/b/isbn/9788532522663-L.jpg",
    cor: "#963f38"
  },
  {
    id: "demo-2",
    titulo: "O Pequeno Príncipe",
    autor: "Antoine de Saint-Exupéry",
    anoPublicacao: 1943,
    paginas: 96,
    disponivel: true,
    capa: "https://covers.openlibrary.org/b/isbn/9780156012195-L.jpg",
    cor: "#687b9b"
  },
  {
    id: "demo-3",
    titulo: "A Biblioteca da Meia-Noite",
    autor: "Matt Haig",
    anoPublicacao: 2020,
    paginas: 308,
    disponivel: false,
    capa: "https://covers.openlibrary.org/b/isbn/9780525559474-L.jpg",
    cor: "#456b52"
  },
  {
    id: "demo-4",
    titulo: "Orgulho e Preconceito",
    autor: "Jane Austen",
    anoPublicacao: 1813,
    paginas: 424,
    disponivel: true,
    capa: "https://covers.openlibrary.org/b/isbn/9780141439518-L.jpg",
    cor: "#a77951"
  },
  {
    id: "demo-5",
    titulo: "O Hobbit",
    autor: "J. R. R. Tolkien",
    anoPublicacao: 1937,
    paginas: 310,
    disponivel: true,
    capa: "https://covers.openlibrary.org/b/isbn/9780547928227-L.jpg",
    cor: "#718052"
  },
  {
    id: "demo-6",
    titulo: "Dom Casmurro",
    autor: "Machado de Assis",
    anoPublicacao: 1899,
    paginas: 256,
    disponivel: false,
    capa: "https://covers.openlibrary.org/b/isbn/9788535902778-L.jpg",
    cor: "#62566f"
  },
  {
    id: "demo-7",
    titulo: "1984",
    autor: "George Orwell",
    anoPublicacao: 1949,
    paginas: 328,
    disponivel: true,
    capa: "https://covers.openlibrary.org/b/isbn/9780451524935-L.jpg",
    cor: "#5b6574"
  },
  {
    id: "demo-8",
    titulo: "Coraline",
    autor: "Neil Gaiman",
    anoPublicacao: 2002,
    paginas: 208,
    disponivel: true,
    capa: "https://covers.openlibrary.org/b/isbn/9780380807345-L.jpg",
    cor: "#384c63"
  }
];

let books = [...demoBooks];
let loans = [];
let history = [];
let authMode = "login";
let isDemo = true;
let token = sessionStorage.getItem("bibliotecaToken") || "";

function notify(message) {
  const toast = $("#toast");
  toast.textContent = message;
  toast.classList.add("show");
  clearTimeout(notify.timer);
  notify.timer = setTimeout(() => toast.classList.remove("show"), 3200);
}

function escapeHTML(value = "") {
  return String(value).replace(/[&<>"']/g, (char) => ({
    "&": "&amp;",
    "<": "&lt;",
    ">": "&gt;",
    '"': "&quot;",
    "'": "&#39;"
  })[char]);
}

function getBookTitle(book) {
  return book.titulo ?? book.tituloLivro ?? book.title ?? "Livro sem título";
}

function getBookAuthor(book) {
  return book.autor?.nome ?? book.autor ?? book.nomeAutor ?? "Autor não informado";
}

function getBookId(book) {
  return book.id ?? book.idLivro;
}

function getBookCover(book) {
  return book.capa ?? book.coverUrl ?? book.urlCapa ?? "";
}

function isAvailable(book) {
  if (typeof book.disponivel === "boolean") return book.disponivel;
  if (typeof book.available === "boolean") return book.available;
  return true;
}

function coverMarkup(book) {
  const title = escapeHTML(getBookTitle(book));
  const author = escapeHTML(getBookAuthor(book));
  const cover = getBookCover(book);
  const color = book.cor || "#526b4c";

  if (cover) {
    return `
      <img class="book-cover"
           src="${escapeHTML(cover)}"
           alt="Capa de ${title}"
           loading="lazy"
           onerror="this.outerHTML='<div class=&quot;cover-fallback&quot; style=&quot;--cover:${color}&quot;><small>BIBLIOTECA PONTILHADA</small><strong>${title}</strong><small>${author}</small></div>'">
    `;
  }

  return `
    <div class="cover-fallback" style="--cover:${color}">
      <small>BIBLIOTECA PONTILHADA</small>
      <strong>${title}</strong>
      <small>${author}</small>
    </div>
  `;
}

function renderBooks() {
  const search = $("#searchInput").value.trim().toLocaleLowerCase("pt-BR");
  const availability = $("#availabilityFilter").value;

  const filtered = books.filter((book) => {
    const title = getBookTitle(book).toLocaleLowerCase("pt-BR");
    const author = getBookAuthor(book).toLocaleLowerCase("pt-BR");
    const matchesSearch = title.includes(search) || author.includes(search);
    const available = isAvailable(book);

    const matchesAvailability =
      availability === "todos" ||
      (availability === "disponivel" && available) ||
      (availability === "emprestado" && !available);

    return matchesSearch && matchesAvailability;
  });

  $("#bookCount").textContent = filtered.length;
  $("#emptyState").hidden = filtered.length > 0;

  $("#bookGrid").innerHTML = filtered.map((book) => {
    const id = escapeHTML(getBookId(book) ?? "");
    const title = escapeHTML(getBookTitle(book));
    const author = escapeHTML(getBookAuthor(book));
    const available = isAvailable(book);
    const year = book.anoPublicacao ?? book.ano ?? "";

    return `
      <article class="book-card" data-book-id="${id}">
        <div class="cover-wrap" data-action="details" data-id="${id}">
          ${coverMarkup(book)}
          <span class="status-pill ${available ? "" : "out"}">
            ${available ? "Disponível" : "Emprestado"}
          </span>
        </div>
        <div class="book-info">
          <h3>${title}</h3>
          <p class="book-author">${author}</p>
          <div class="book-meta">
            <span>${escapeHTML(year || "Ano não informado")}</span>
            <span>${book.paginas ? `${escapeHTML(book.paginas)} pág.` : ""}</span>
          </div>
          <div class="book-actions">
            <button data-action="details" data-id="${id}">Detalhes</button>
            ${isDemo ? "" : `<button data-action="edit" data-id="${id}">Editar</button>`}
          </div>
        </div>
      </article>
    `;
  }).join("");

  renderManageBooks();
  refreshLoanOptions();
}

function renderManageBooks() {
  const container = $("#manageBookList");
  if (!container) return;

  if (books.length === 0) {
    container.textContent = "Nenhum livro carregado.";
    return;
  }

  container.innerHTML = books.map((book) => `
    <div class="list-row">
      <div>
        <strong>${escapeHTML(getBookTitle(book))}</strong>
        <small>${escapeHTML(getBookAuthor(book))}</small>
      </div>
      <div class="book-actions">
        <button data-action="edit" data-id="${escapeHTML(getBookId(book) ?? "")}">Editar</button>
        ${isDemo ? "" : `<button class="delete-button" data-action="delete" data-id="${escapeHTML(getBookId(book) ?? "")}">Excluir</button>`}
      </div>
    </div>
  `).join("");
}

function refreshLoanOptions() {
  const select = $("#loanBook");
  if (!select) return;

  const previous = select.value;
  select.innerHTML = '<option value="">Selecione um livro</option>';

  books.filter(isAvailable).forEach((book) => {
    const option = document.createElement("option");
    option.value = getBookId(book);
    option.textContent = getBookTitle(book);
    select.appendChild(option);
  });

  if ([...select.options].some((option) => option.value === previous)) {
    select.value = previous;
  }
}

function showBookDetails(id) {
  const book = books.find((item) => String(getBookId(item)) === String(id));
  if (!book) return;

  $("#bookDetails").innerHTML = `
    <div class="dialog-details">
      ${coverMarkup(book).replace('class="book-cover"', 'class="dialog-cover"')}
      <div>
        <p class="eyebrow">DETALHES DA OBRA</p>
        <h2>${escapeHTML(getBookTitle(book))}</h2>
        <p>Por ${escapeHTML(getBookAuthor(book))}</p>
        <p>Ano: ${escapeHTML(book.anoPublicacao ?? book.ano ?? "Não informado")}</p>
        <p>Páginas: ${escapeHTML(book.paginas ?? "Não informado")}</p>
        <p>${isAvailable(book) ? "Esta obra está disponível no acervo." : "Esta obra está emprestada no momento."}</p>
      </div>
    </div>
  `;

  $("#bookDialog").showModal();
}

function showPage(name) {
  const pageNames = ["catalogo", "emprestimos", "historico", "livros", "usuarios"];

  pageNames.forEach((page) => {
    $(`#${page}Page`).classList.toggle("hidden", page !== name);
  });

  $$(".nav-item[data-page]").forEach((button) => {
    button.classList.toggle("active", button.dataset.page === name);
  });

  const labels = {
    catalogo: "Catálogo",
    emprestimos: "Empréstimos",
    historico: "Histórico",
    livros: "Livros",
    usuarios: "Usuários"
  };

  $("#breadcrumbPage").textContent = labels[name] || "Catálogo";
}

async function apiRequest(path, options = {}) {
  const headers = { ...(options.headers || {}) };

  if (options.body && !(options.body instanceof FormData)) {
    headers["Content-Type"] = "application/json";
  }
  if (token) headers.Authorization = `Bearer ${token}`;

  const response = await fetch(`${API.base}${path}`, {
    ...options,
    headers
  });

  const contentType = response.headers.get("content-type") || "";
  const data = contentType.includes("application/json")
    ? await response.json()
    : await response.text();

  if (!response.ok) {
    const message = typeof data === "object"
      ? data.message || data.mensagem || data.error
      : data;
    throw new Error(message || `Erro HTTP ${response.status}`);
  }

  return data;
}

async function loadBooks() {
  if (!API.books) {
    isDemo = true;
    books = [...demoBooks];
    renderBooks();
    notify("Catálogo de demonstração. A API de livros ainda não foi configurada.");
    return;
  }

  try {
    const data = await apiRequest(API.books);
    books = Array.isArray(data) ? data : (data.content || data.livros || []);
    isDemo = false;
    renderBooks();
  } catch (error) {
    notify(`Não foi possível carregar os livros: ${error.message}`);
  }
}

function clearBookForm() {
  $("#bookForm").reset();
  $("#editBookId").value = "";
  $("#bookFormTitle").textContent = "Cadastrar livro";
}

function editBook(id) {
  if (isDemo) {
    notify("A edição persistente será ativada após configurar a API real.");
    return;
  }

  const book = books.find((item) => String(getBookId(item)) === String(id));
  if (!book) return;

  $("#editBookId").value = getBookId(book);
  $("#bookTitle").value = getBookTitle(book);
  $("#bookAuthor").value = getBookAuthor(book);
  $("#bookYear").value = book.anoPublicacao ?? book.ano ?? "";
  $("#bookPages").value = book.paginas ?? "";
  $("#bookCover").value = getBookCover(book);
  $("#bookFormTitle").textContent = "Editar livro";
  showPage("livros");
  $("#bookTitle").focus();
}

async function deleteBook(id) {
  if (!API.books || isDemo) {
    notify("Configure a rota real de exclusão antes de excluir livros.");
    return;
  }

  if (!confirm("Deseja realmente excluir este livro?")) return;

  try {
    await apiRequest(`${API.books}/${encodeURIComponent(id)}`, { method: "DELETE" });
    books = books.filter((book) => String(getBookId(book)) !== String(id));
    renderBooks();
    notify("Livro excluído.");
  } catch (error) {
    notify(`Não foi possível excluir: ${error.message}`);
  }
}

async function saveBook(event) {
  event.preventDefault();

  if (!API.books || isDemo) {
    notify("O formulário está pronto, mas precisamos confirmar a rota e os campos do LivroController.");
    return;
  }

  const id = $("#editBookId").value;

  // Ajuste os nomes abaixo conforme os atributos reais da entidade Livro.
  const payload = {
    titulo: $("#bookTitle").value.trim(),
    autor: $("#bookAuthor").value.trim(),
    anoPublicacao: $("#bookYear").value ? Number($("#bookYear").value) : null
  };

  try {
    const path = id
      ? `${API.books}/${encodeURIComponent(id)}`
      : API.books;

    const saved = await apiRequest(path, {
      method: id ? "PUT" : "POST",
      body: JSON.stringify(payload)
    });

    if (id) {
      books = books.map((book) =>
        String(getBookId(book)) === id ? saved : book
      );
    } else {
      books.push(saved);
    }

    clearBookForm();
    renderBooks();
    notify("Livro salvo.");
  } catch (error) {
    notify(`Não foi possível salvar: ${error.message}`);
  }
}

function renderLoans() {
  $("#loanList").innerHTML = loans.length
    ? loans.map((loan, index) => `
      <div class="list-row">
        <div>
          <strong>${escapeHTML(loan.titulo)}</strong>
          <small>Usuário: ${escapeHTML(loan.userId)} · ${escapeHTML(loan.status)}</small>
        </div>
        <button data-action="return-demo" data-index="${index}">Devolver (demo)</button>
      </div>
    `).join("")
    : "Nenhum empréstimo carregado.";
}

function renderHistory() {
  $("#historyList").innerHTML = history.length
    ? history.map((item) => `
      <div class="list-row">
        <div>
          <strong>${escapeHTML(item.titulo)}</strong>
          <small>${escapeHTML(item.evento)} · ${escapeHTML(item.data)}</small>
        </div>
      </div>
    `).join("")
    : "Nenhum histórico carregado.";
}

async function submitLoan(event) {
  event.preventDefault();

  if (!API.loans || isDemo) {
    notify("Para registrar empréstimos de verdade, precisamos configurar a rota real do backend.");
    return;
  }

  const payload = {
    livroId: Number($("#loanBook").value),
    usuarioId: Number($("#loanUserId").value)
  };

  try {
    await apiRequest(API.loans, {
      method: "POST",
      body: JSON.stringify(payload)
    });
    notify("Empréstimo registrado.");
    await loadBooks();
  } catch (error) {
    notify(`Não foi possível registrar o empréstimo: ${error.message}`);
  }
}

async function submitAuth(event) {
  event.preventDefault();

  const username = $("#authUsername").value.trim();
  const password = $("#authPassword").value;

  try {
    if (authMode === "register") {
      // Confirme os campos obrigatórios de Usuario.java antes de usar.
      const payload = {
        nome: $("#authName").value.trim(),
        username,
        senha: password
      };

      await apiRequest(API.register, {
        method: "POST",
        body: JSON.stringify(payload)
      });

      notify("Cadastro enviado. Agora tente entrar.");
      setAuthMode("login");
      return;
    }

    const result = await apiRequest(API.login, {
      method: "POST",
      body: JSON.stringify({ username, senha: password })
    });

    const receivedToken = result.token || result.accessToken || result.jwt;

    if (receivedToken) {
      token = receivedToken;
      sessionStorage.setItem("bibliotecaToken", token);
    }

    $("#userName").textContent = result.nome || result.username || username;
    notify("Resposta de login recebida.");
    showPage("catalogo");
    await loadBooks();
  } catch (error) {
    notify(`Falha na autenticação: ${error.message}. Confira os campos esperados pelo backend.`);
  }
}

function setAuthMode(mode) {
  authMode = mode;
  $$(".auth-tab").forEach((tab) => {
    tab.classList.toggle("active", tab.dataset.auth === mode);
  });
  $("#registerFields").hidden = mode !== "register";
  $("#authName").required = mode === "register";
  $("#authSubmit").textContent = mode === "register" ? "Criar conta" : "Entrar";
  $("#authPassword").autocomplete = mode === "register" ? "new-password" : "current-password";
}

document.addEventListener("click", (event) => {
  const nav = event.target.closest("[data-page]");
  if (nav) {
    event.preventDefault();
    showPage(nav.dataset.page);
  }

  const action = event.target.closest("[data-action]");
  if (!action) return;

  const { action: type, id, index } = action.dataset;

  if (type === "details") showBookDetails(id);
  if (type === "edit") editBook(id);
  if (type === "delete") deleteBook(id);

  if (type === "return-demo") {
    if (!isDemo) {
      notify("Configure a rota real de devolução antes de concluir esta operação.");
      return;
    }

    const loan = loans[Number(index)];
    if (!loan) return;

    loans.splice(Number(index), 1);
    const book = books.find((item) => String(getBookId(item)) === String(loan.bookId));
    if (book) book.disponivel = true;

    history.unshift({
      titulo: loan.titulo,
      evento: "Devolução de demonstração",
      data: new Date().toLocaleDateString("pt-BR")
    });

    renderBooks();
    renderLoans();
    renderHistory();
    notify("Devolução simulada. Nenhuma alteração foi enviada ao backend.");
  }
});

$$(".auth-tab").forEach((tab) => {
  tab.addEventListener("click", () => setAuthMode(tab.dataset.auth));
});

$("#searchInput").addEventListener("input", renderBooks);
$("#availabilityFilter").addEventListener("change", renderBooks);
$("#bookForm").addEventListener("submit", saveBook);
$("#cancelEdit").addEventListener("click", clearBookForm);
$("#loanForm").addEventListener("submit", submitLoan);
$("#authForm").addEventListener("submit", submitAuth);
$("#closeDialog").addEventListener("click", () => $("#bookDialog").close());

$("#logoutButton").addEventListener("click", () => {
  token = "";
  sessionStorage.removeItem("bibliotecaToken");
  $("#userName").textContent = "Leitor";
  notify("Token local removido.");
  showPage("catalogo");
});

renderBooks();
renderLoans();
renderHistory();
showPage("catalogo");
setAuthMode("login");
