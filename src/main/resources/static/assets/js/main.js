/* =============================================
   Slide Toggle Utilities (inchangé)
   ============================================= */
function slideToggle(t, e, o) {
  0 === t.clientHeight ? j(t, e, o, !0) : j(t, e, o);
}
function slideUp(t, e, o) { j(t, e, o); }
function slideDown(t, e, o) { j(t, e, o, !0); }
function j(t, e, o, i) {
  void 0 === e && (e = 400);
  void 0 === i && (i = !1);
  t.style.overflow = "hidden";
  i && (t.style.display = "block");
  var p, l = window.getComputedStyle(t),
    n = parseFloat(l.getPropertyValue("height")),
    a = parseFloat(l.getPropertyValue("padding-top")),
    s = parseFloat(l.getPropertyValue("padding-bottom")),
    r = parseFloat(l.getPropertyValue("margin-top")),
    d = parseFloat(l.getPropertyValue("margin-bottom")),
    g = n / e, y = a / e, m = s / e, u = r / e, h = d / e;
  window.requestAnimationFrame(function l(x) {
    void 0 === p && (p = x);
    var f = x - p;
    i
      ? (t.style.height = g * f + "px", t.style.paddingTop = y * f + "px", t.style.paddingBottom = m * f + "px", t.style.marginTop = u * f + "px", t.style.marginBottom = h * f + "px")
      : (t.style.height = n - g * f + "px", t.style.paddingTop = a - y * f + "px", t.style.paddingBottom = s - m * f + "px", t.style.marginTop = r - u * f + "px", t.style.marginBottom = d - h * f + "px");
    f >= e
      ? (t.style.height = "", t.style.paddingTop = "", t.style.paddingBottom = "", t.style.marginTop = "", t.style.marginBottom = "", t.style.overflow = "", i || (t.style.display = "none"), "function" == typeof o && o())
      : window.requestAnimationFrame(l);
  });
}

/* =============================================
   Sidebar Multi-Level Submenu (récursif)
   ============================================= */

/**
 * Initialise les submenus imbriqués pour un conteneur donné.
 * Appelé récursivement pour chaque niveau de profondeur.
 *
 * @param {Element} container - L'élément parent dans lequel chercher les items
 */
function initNestedSubmenus(container) {
  // Sélectionne uniquement les enfants directs (évite les doublons sur les niveaux déjà traités)
  const items = container.querySelectorAll(':scope > .submenu-item.has-sub, :scope > li.sidebar-item.has-sub');

  items.forEach(function (item) {
    const link = item.querySelector(':scope > .submenu-link, :scope > .sidebar-link');
    const submenu = item.querySelector(':scope > .submenu');

    if (!link || !submenu) return;

    // Masquer le submenu au départ s'il n'est pas actif
    if (!submenu.classList.contains('active')) {
      submenu.style.display = 'none';
    }

    link.addEventListener('click', function (e) {
      e.preventDefault();
      e.stopPropagation(); // Empêche la propagation au niveau parent

      const isOpen = submenu.classList.contains('active');

      // Pour les submenus de niveau 1 (.sidebar-item), fermer les siblings
      if (item.classList.contains('sidebar-item')) {
        closeSiblings(item);
      }

      if (isOpen) {
        // Fermer ce submenu
        submenu.classList.remove('active');
        slideUp(submenu, 250);
        item.classList.remove('open');
        rotateArrow(link, false);
      } else {
        // Ouvrir ce submenu
        submenu.classList.add('active');
        if (submenu.style.display === 'none') {
          submenu.classList.add('active');
        }
        slideDown(submenu, 250);
        item.classList.add('open');
        rotateArrow(link, true);
      }
    });

    // Récursion : initialiser les sous-niveaux dans ce submenu
    initNestedSubmenus(submenu);
  });
}

/**
 * Ferme les submenus frères (siblings) du même niveau.
 * Utilisé pour les items de niveau 1 uniquement (comportement accordion).
 */
function closeSiblings(item) {
  const parent = item.parentElement;
  if (!parent) return;

  const siblings = parent.querySelectorAll(':scope > .sidebar-item.has-sub.open');
  siblings.forEach(function (sibling) {
    if (sibling === item) return;
    const sibSubmenu = sibling.querySelector(':scope > .submenu');
    const sibLink = sibling.querySelector(':scope > .sidebar-link');
    if (sibSubmenu) {
      sibSubmenu.classList.remove('active');
      slideUp(sibSubmenu, 200);
    }
    sibling.classList.remove('open');
    if (sibLink) rotateArrow(sibLink, false);
  });
}

/**
 * Fait pivoter la flèche chevron d'un lien.
 */
function rotateArrow(link, open) {
  const arrow = link.querySelector('.submenu-arrow');
  if (arrow) {
    arrow.style.transform = open ? 'rotate(90deg)' : 'rotate(0deg)';
    arrow.style.transition = 'transform 0.25s ease';
  }
}

/* =============================================
   Initialisation au chargement du DOM
   ============================================= */
document.addEventListener('DOMContentLoaded', function () {

  // Lance l'initialisation récursive depuis la racine du menu
  const menuRoot = document.querySelector('.sidebar-menu .menu');
  if (menuRoot) {
    initNestedSubmenus(menuRoot);
  }

  // Ouvre automatiquement le(s) submenu(s) contenant un lien actif
  const activeLinks = document.querySelectorAll('.sidebar-item .submenu-item a.active, .sidebar-item a.active');
  activeLinks.forEach(function (activeLink) {
    // Remonter la hiérarchie pour ouvrir tous les parents
    let parent = activeLink.closest('.submenu');
    while (parent) {
      parent.classList.add('active');
      parent.style.display = 'block';
      const parentItem = parent.closest('.has-sub');
      if (parentItem) {
        parentItem.classList.add('open');
        const parentLink = parentItem.querySelector(':scope > .sidebar-link, :scope > .submenu-link');
        if (parentLink) rotateArrow(parentLink, true);
      }
      parent = parent.parentElement ? parent.parentElement.closest('.submenu') : null;
    }
  });

  // Responsive : masquer sidebar sur petits écrans
  if (window.innerWidth < 1200) {
    const sidebar = document.getElementById('sidebar');
    if (sidebar) sidebar.classList.remove('active');
  }
});

/* =============================================
   Responsive resize
   ============================================= */
window.addEventListener('resize', function () {
  var w = window.innerWidth;
  const sidebar = document.getElementById('sidebar');
  if (!sidebar) return;
  if (w < 1200) {
    sidebar.classList.remove('active');
  } else {
    sidebar.classList.add('active');
  }
});

/* =============================================
   Boutons toggle burger / hide
   ============================================= */
const burgerBtn = document.querySelector('.burger-btn');
if (burgerBtn) {
  burgerBtn.addEventListener('click', function () {
    document.getElementById('sidebar').classList.toggle('active');
  });
}

const sidebarHide = document.querySelector('.sidebar-hide');
if (sidebarHide) {
  sidebarHide.addEventListener('click', function () {
    document.getElementById('sidebar').classList.toggle('active');
  });
}

/* =============================================
   Perfect Scrollbar
   ============================================= */
if (typeof PerfectScrollbar === 'function') {
  const container = document.querySelector('.sidebar-wrapper');
  if (container) {
    new PerfectScrollbar(container, { wheelPropagation: false });
  }
}

/* =============================================
   Scroll vers l'item actif
   ============================================= */
const activeItem = document.querySelector('.sidebar-item.active');
if (activeItem) {
  activeItem.scrollIntoView(false);
}

/* =============================================
   CSS dynamique pour les niveaux imbriqués
   ============================================= */
(function injectNestedStyles() {
  const style = document.createElement('style');
  style.textContent = `
    /* Flèche de navigation pour les sous-niveaux */
    .submenu-link {
      display: flex;
      align-items: center;
      justify-content: space-between;
      cursor: pointer;
      padding: 8px 20px;
      color: inherit;
      text-decoration: none;
      transition: background 0.2s;
    }
    .submenu-link:hover {
      background: rgba(255,255,255,0.07);
      border-radius: 6px;
    }
    .submenu-arrow {
      font-size: 0.75rem;
      margin-left: auto;
      flex-shrink: 0;
      transition: transform 0.25s ease;
    }

    /* Indentation par niveau */
    .submenu-level-2 {
      padding-left: 12px;
    }
    .submenu-level-3 {
      padding-left: 12px;
    }

    /* Ligne verticale indicatrice de niveau */
    .submenu-level-2 > .submenu-item > a,
    .submenu-level-2 > .submenu-item > .submenu-link {
      border-left: 2px solid rgba(255,255,255,0.12);
      padding-left: 14px;
      font-size: 0.875rem;
    }
    .submenu-level-3 > .submenu-item > a,
    .submenu-level-3 > .submenu-item > .submenu-link {
      border-left: 2px solid rgba(255,255,255,0.08);
      padding-left: 14px;
      font-size: 0.83rem;
      opacity: 0.88;
    }

    /* Item ouvert */
    .sidebar-item.open > .sidebar-link,
    .submenu-item.open > .submenu-link {
      color: #435ebe;
    }
  `;
  document.head.appendChild(style);
})();