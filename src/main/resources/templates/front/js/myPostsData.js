/* ══════════════════════════════════════════
   CAROUSEL
══════════════════════════════════════════ */
const carState = {};

function slideStop(e, id, dir) {
  e.stopPropagation();
  const track = document.getElementById(id);
  const slides = track.children.length;

  if (!carState[id]) carState[id] = 0;

  carState[id] = (carState[id] + dir + slides) % slides;
  track.style.transform = `translateX(-${carState[id] * 100}%)`;

  const card = track.closest('.carousel');
  card.querySelectorAll('.carousel-dot').forEach((d, i) => {
    d.classList.toggle('active', i === carState[id]);
  });

  const cnt = card.querySelector('.carousel-count');
  if (cnt) cnt.textContent = `${carState[id] + 1} / ${slides}`;
}

/* ══════════════════════════════════════════
   FILTER CHIPS
══════════════════════════════════════════ */
document.querySelectorAll('.filter-chip').forEach(chip => {
  chip.addEventListener('click', () => {
    document.querySelectorAll('.filter-chip').forEach(c => c.classList.remove('active'));
    chip.classList.add('active');
  });
});

/* ══════════════════════════════════════════
   COMPOSER
══════════════════════════════════════════ */
const composerBody = document.getElementById('composer-body');
const composerTrigger = document.getElementById('composer-trigger');
const composerCancel = document.getElementById('composer-cancel');
const openBtn = document.getElementById('open-composer-btn');

function openComposer() {
  composerBody.classList.add('open');
  composerTrigger.style.display = 'none';
  document.getElementById('c-title').focus();
}

function closeComposer() {
  composerBody.classList.remove('open');
  composerTrigger.style.display = 'flex';
}

composerTrigger.addEventListener('click', openComposer);
openBtn.addEventListener('click', openComposer);
composerCancel.addEventListener('click', closeComposer);

document.getElementById('composer-submit-btn').addEventListener('click', () => {
  const title = document.getElementById('c-title').value.trim();

  if (!title) {
    document.getElementById('c-title').focus();
    return;
  }

  alert('Raportul a fost publicat!');
  closeComposer();

  document.getElementById('c-title').value = '';
  document.getElementById('c-desc').value = '';
  document.getElementById('c-addr').value = '';
});

document.getElementById('geo-btn-composer').addEventListener('click', () => {
  if (!navigator.geolocation) return;

  navigator.geolocation.getCurrentPosition(pos => {
    alert(`GPS detectat:\nLat: ${pos.coords.latitude.toFixed(5)}\nLng: ${pos.coords.longitude.toFixed(5)}`);
  });
});

/* ══════════════════════════════════════════
   TIMELINE MODAL
══════════════════════════════════════════ */
const backdrop = document.getElementById('modal-backdrop');
const panelClose = document.getElementById('panel-close-btn');

function stateIcon(state) {
  if (state === 'done') {
    return `<svg width="10" height="10" viewBox="0 0 10 10" fill="none" stroke="#16a34a" stroke-width="2" stroke-linecap="round"><path d="M1.5 5l2.5 2.5 5-5"/></svg>`;
  }

  if (state === 'active') {
    return `<svg width="8" height="8" viewBox="0 0 8 8"><circle cx="4" cy="4" r="3.5" fill="#2563eb"/></svg>`;
  }

  return '';
}

function openModal(postId) {
  const p = posts[postId];
  if (!p) return;

  document.getElementById('panel-title').textContent = p.title;
  document.getElementById('panel-meta').textContent = p.meta;

  const instHtml = p.institution.hasInst
    ? `<div class="institution-box">
        <div class="inst-icon">
          <svg width="16" height="16" viewBox="0 0 18 18" fill="none" stroke="#fff" stroke-width="1.6" stroke-linecap="round"><rect x="3" y="7" width="12" height="9" rx="1"/><path d="M1 7l8-5 8 5"/><path d="M7 16v-5h4v5"/></svg>
        </div>
        <div class="inst-info">
          <div class="inst-title">${p.institution.name}</div>
          <div class="inst-sub">${p.institution.ref}</div>
        </div>
      </div>`
    : `<div class="institution-box none">
        <div class="inst-icon">
          <svg width="16" height="16" viewBox="0 0 18 18" fill="none" stroke="#9ca3af" stroke-width="1.6" stroke-linecap="round"><rect x="3" y="7" width="12" height="9" rx="1"/><path d="M1 7l8-5 8 5"/><path d="M7 16v-5h4v5"/></svg>
        </div>
        <div class="inst-info">
          <div class="inst-title" style="color:var(--text-muted,#9ca3af)">Nicio instituție nu a preluat încă</div>
          <div class="inst-sub">Raportul este în curs de procesare internă</div>
        </div>
      </div>`;

  const tlItems = p.timeline.map(item => `
    <div class="tl-item ${item.state}">
      <div class="tl-dot ${item.state}">${stateIcon(item.state)}</div>
      <div class="tl-content">
        <div class="tl-top">
          <span class="tl-status">${item.status}</span>
          ${item.date ? `<span class="tl-date">${item.date}</span>` : '<span class="tl-date" style="font-style:italic">în așteptare</span>'}
        </div>
        <div class="tl-desc">${item.desc}</div>
        ${item.actor ? `<div class="tl-actor">
          <svg width="10" height="10" viewBox="0 0 10 10" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"><circle cx="5" cy="3" r="2"/><path d="M1 9c0-2.21 1.79-4 4-4s4 1.79 4 4"/></svg>
          ${item.actor}
        </div>` : ''}
      </div>
    </div>
  `).join('');

  document.getElementById('panel-body').innerHTML = `
    <div class="panel-current-status">
      <span class="status-badge ${p.currentStatusClass}">${p.currentStatus}</span>
      <div>
        <div class="pcs-label">Status curent</div>
        <div class="pcs-val">${p.currentStatus}</div>
      </div>
    </div>
    ${instHtml}
    <div class="timeline-label">Istoric procesare</div>
    <div class="timeline">${tlItems}</div>
    <div class="panel-eta">
      <span class="eta-label">⏱ Estimare</span>
      <span class="eta-val">${p.eta}</span>
    </div>
  `;

  backdrop.classList.add('visible');
  document.body.style.overflow = 'hidden';
}

function closeModal() {
  backdrop.classList.remove('visible');
  document.body.style.overflow = '';
}

document.querySelectorAll('.post-card[data-post-id]').forEach(card => {
  card.addEventListener('click', (e) => {
    if (e.target.closest('.carousel-btn') || e.target.closest('.action-btn')) return;
    openModal(card.dataset.postId);
  });
});

panelClose.addEventListener('click', closeModal);

backdrop.addEventListener('click', (e) => {
  if (e.target === backdrop) closeModal();
});

document.addEventListener('keydown', (e) => {
  if (e.key === 'Escape') closeModal();
});