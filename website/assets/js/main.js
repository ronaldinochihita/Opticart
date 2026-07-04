/* =========================================================
   Opticart — Interações do site de divulgação
   ========================================================= */

(() => {
  'use strict';

  /* ----- 1. Menu mobile (hambúrguer) --------------------- */
  // Só faz sentido em ecrãs estreitos, mas o listener não faz mal
  // em desktop porque o botão está escondido via CSS.
  const toggle = document.querySelector('.nav-toggle');
  const nav    = document.querySelector('.nav-primary');

  if (toggle && nav) {
    toggle.addEventListener('click', () => {
      const aberto = nav.classList.toggle('open');
      toggle.setAttribute('aria-expanded', aberto ? 'true' : 'false');
      toggle.setAttribute('aria-label', aberto ? 'Fechar menu' : 'Abrir menu');
    });

    // Fecha o menu ao clicar num link (comodidade em mobile)
    nav.querySelectorAll('a').forEach((link) => {
      link.addEventListener('click', () => {
        if (nav.classList.contains('open')) {
          nav.classList.remove('open');
          toggle.setAttribute('aria-expanded', 'false');
          toggle.setAttribute('aria-label', 'Abrir menu');
        }
      });
    });
  }

  /* ----- 2. Realçar link do menu conforme a secção visível -- */
  // Usa IntersectionObserver para não custar performance.
  const seccoes = document.querySelectorAll('section[id]');
  const linksNav = document.querySelectorAll('.nav-primary a[href^="#"]');

  if (seccoes.length && linksNav.length && 'IntersectionObserver' in window) {
    const mapa = new Map();
    linksNav.forEach((a) => {
      const id = a.getAttribute('href').slice(1);
      if (id) mapa.set(id, a);
    });

    const observer = new IntersectionObserver((entries) => {
      entries.forEach((entry) => {
        const link = mapa.get(entry.target.id);
        if (!link) return;
        if (entry.isIntersecting) {
          linksNav.forEach((l) => l.classList.remove('active'));
          link.classList.add('active');
        }
      });
    }, {
      // Um pouco abaixo do header para ativar quando a secção está mesmo à vista
      rootMargin: '-40% 0px -55% 0px',
      threshold: 0
    });

    seccoes.forEach((s) => observer.observe(s));
  }

  /* ----- 3. Fade-in das secções ao entrar no viewport ------- */
  // Progressive enhancement: se o utilizador prefere reduced motion,
  // não animamos nada.
  const preferReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

  if (!preferReducedMotion && 'IntersectionObserver' in window) {
    const alvos = document.querySelectorAll('.section, .hero, .feature, .persona, .team-card');
    alvos.forEach((el) => {
      el.style.opacity = '0';
      el.style.transform = 'translateY(16px)';
      el.style.transition = 'opacity .6s ease-out, transform .6s ease-out';
    });

    const io = new IntersectionObserver((entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          const el = entry.target;
          el.style.opacity = '1';
          el.style.transform = 'none';
          io.unobserve(el);
        }
      });
    }, { threshold: 0.12 });

    alvos.forEach((el) => io.observe(el));
  }

  /* ----- 4. Ano corrente no rodapé ------------------------- */
  // Actualiza automaticamente para o ano actual, caso um dia
  // esqueça de mudar o HTML.
  const anoAtual = new Date().getFullYear();
  document.querySelectorAll('[data-ano]').forEach((el) => {
    el.textContent = anoAtual;
  });

})();
