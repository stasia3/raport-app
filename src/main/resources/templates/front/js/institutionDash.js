(function () {
  const FALLBACK_TICKETS = [
    {
      id: 101,
      ticket_number: 'TK-4101',
      problem_tag: 'Salubritate',
      address: 'Str. Lalelelor nr. 12, Galați',
      gps_lat: 45.43591,
      gps_long: 28.04442,
      description: 'Platforma de gunoi este plină, deșeurile au ajuns pe trotuar și blochează accesul către scară.',
      status: 'Trimis',
      ranking_score: 88,
      is_red_flag: true,
      assigned_institution_user_id: 301,
      institution_name: 'Serviciu Salubritate Municipal',
      institution_service_type: 'Salubritate',
      created_at: new Date(Date.now() - 11 * 60 * 60 * 1000).toISOString(),
      before_photo_url: '',
      after_photo_url: '',
      activity_history: [],
      status_history: [
        createStatusHistory('Trimis', null, 'Dispatcher demo', 'Trimis spre instituție pentru intervenție.')
      ],
      documents: []
    },
    {
      id: 102,
      ticket_number: 'TK-4102',
      problem_tag: 'Iluminat',
      address: 'Bd. Unirii nr. 44, București Sector 4',
      gps_lat: 44.41221,
      gps_long: 26.12221,
      description: 'Doi stâlpi de iluminat nu funcționează, iar zona este slab vizibilă seara.',
      status: 'In_Lucru',
      ranking_score: 70,
      is_red_flag: false,
      assigned_institution_user_id: 302,
      institution_name: 'Electrica — Iluminat Public',
      institution_service_type: 'Iluminat',
      created_at: new Date(Date.now() - 27 * 60 * 60 * 1000).toISOString(),
      before_photo_url: '',
      after_photo_url: '',
      activity_history: [
        { note: 'Echipa a verificat tabloul electric.', actor: 'Operator Iluminat', created_at: new Date(Date.now() - 8 * 60 * 60 * 1000).toISOString() }
      ],
      status_history: [
        createStatusHistory('Trimis', null, 'Dispatcher demo', 'Trimis spre instituție.'),
        createStatusHistory('Preluat', 'Trimis', 'Operator Iluminat', 'Tichet preluat de instituție.'),
        createStatusHistory('In_Lucru', 'Preluat', 'Operator Iluminat', 'Intervenție programată pentru teren.')
      ],
      documents: [
        { name: 'constatare-initiala.pdf', size_label: '184 KB', uploaded_at: new Date(Date.now() - 7 * 60 * 60 * 1000).toISOString(), actor: 'Operator Iluminat' }
      ]
    },
    {
      id: 103,
      ticket_number: 'TK-4103',
      problem_tag: 'Drumuri',
      address: 'Str. Siderurgiștilor nr. 7, Galați',
      gps_lat: 45.42991,
      gps_long: 28.03772,
      description: 'Groapă mare în carosabil, afectează banda 1 și există risc de avarie auto.',
      status: 'Trimis',
      ranking_score: 96,
      is_red_flag: true,
      assigned_institution_user_id: 303,
      institution_name: 'RADP — Reparații Drumuri',
      institution_service_type: 'Drumuri',
      created_at: new Date(Date.now() - 4 * 60 * 60 * 1000).toISOString(),
      before_photo_url: '',
      after_photo_url: '',
      activity_history: [],
      status_history: [
        createStatusHistory('Trimis', null, 'Dispatcher demo', 'Intervenție urgentă solicitată.')
      ],
      documents: []
    },
    {
      id: 104,
      ticket_number: 'TK-4104',
      problem_tag: 'Salubritate',
      address: 'Aleea Mărășești nr. 5, Galați',
      gps_lat: 45.44013,
      gps_long: 28.05091,
      description: 'Resturi vegetale și saci depozitați ilegal lângă platforma publică.',
      status: 'Preluat',
      ranking_score: 62,
      is_red_flag: false,
      assigned_institution_user_id: 301,
      institution_name: 'Serviciu Salubritate Municipal',
      institution_service_type: 'Salubritate',
      created_at: new Date(Date.now() - 18 * 60 * 60 * 1000).toISOString(),
      before_photo_url: '',
      after_photo_url: '',
      activity_history: [
        { note: 'Intervenția a fost acceptată și planificată pentru schimbul de dimineață.', actor: 'Coordonator Salubritate', created_at: new Date(Date.now() - 14 * 60 * 60 * 1000).toISOString() }
      ],
      status_history: [
        createStatusHistory('Trimis', null, 'Dispatcher demo', 'Tichet direcționat spre serviciul de salubritate.'),
        createStatusHistory('Preluat', 'Trimis', 'Coordonator Salubritate', 'Intervenția a fost acceptată.')
      ],
      documents: []
    }
  ];

  const state = {
    session: null,
    institutionUser: null,
    tickets: [],
    filteredTickets: [],
    selectedTicketId: null,
    pendingAfterPhoto: null,
    localLogs: []
  };

  const dom = {};

  document.addEventListener('DOMContentLoaded', init);

  function init() {
    cacheDom();
    bindEvents();

    const session = readSession();
    if (!session) {
      window.location.href = 'index.html';
      return;
    }

    state.session = session;
    state.institutionUser = normalizeInstitutionUser(session);

    if (state.institutionUser.role !== 'institution') {
      window.location.href = 'index.html';
      return;
    }

    syncProfileUi();
    loadTickets();
  }

  function cacheDom() {
    const ids = [
      'instAvatar', 'institutionName', 'institutionService', 'statusFilter', 'labelFilter', 'searchFilter', 'sortFilter',
      'resetFiltersBtn', 'refreshBtn', 'logoutBtn', 'mobileRefreshBtn', 'mobileLogoutBtn',
      'newTicketsCount', 'workingTicketsCount', 'slaTicketsCount', 'institutionTicketBody', 'institutionEmptyState',
      'ticketCountChip', 'institutionAssignedChip', 'ticketModal', 'closeModalBtn', 'closeFooterBtn',
      'modalTicketNo', 'modalStatusBadge', 'modalRankingChip', 'modalHeaderText', 'beforePhotoBox', 'modalLabel',
      'modalAddress', 'modalGps', 'modalElapsed', 'modalDescription', 'internalNoteInput', 'saveJournalBtn',
      'journalList', 'acceptTicketBtn', 'showRefuseBtn', 'refusalBox', 'refusalReasonInput', 'cancelRefuseBtn',
      'confirmRefuseBtn', 'afterPhotoInput', 'afterPhotoPreview', 'markInWorkBtn', 'resolveTicketBtn', 'pdfInput',
      'uploadPdfBtn', 'pdfList', 'auditPreview', 'toastContainer'
    ];

    ids.forEach((id) => {
      dom[id] = document.getElementById(id);
    });
  }

  function bindEvents() {
    dom.statusFilter.addEventListener('change', applyFilters);
    dom.labelFilter.addEventListener('change', applyFilters);
    dom.searchFilter.addEventListener('input', applyFilters);
    dom.sortFilter.addEventListener('change', applyFilters);
    dom.resetFiltersBtn.addEventListener('click', resetFilters);
    dom.refreshBtn.addEventListener('click', refreshData);
    dom.logoutBtn.addEventListener('click', logout);
    dom.mobileRefreshBtn.addEventListener('click', refreshData);
    dom.mobileLogoutBtn.addEventListener('click', logout);
    dom.closeModalBtn.addEventListener('click', closeModal);
    dom.closeFooterBtn.addEventListener('click', closeModal);
    dom.saveJournalBtn.addEventListener('click', saveJournalNote);
    dom.acceptTicketBtn.addEventListener('click', acceptProject);
    dom.showRefuseBtn.addEventListener('click', () => dom.refusalBox.classList.add('visible'));
    dom.cancelRefuseBtn.addEventListener('click', () => {
      dom.refusalBox.classList.remove('visible');
      dom.refusalReasonInput.value = '';
    });
    dom.confirmRefuseBtn.addEventListener('click', refuseProject);
    dom.afterPhotoInput.addEventListener('change', handleAfterPhotoUpload);
    dom.markInWorkBtn.addEventListener('click', markInWork);
    dom.resolveTicketBtn.addEventListener('click', resolveTicket);
    dom.uploadPdfBtn.addEventListener('click', uploadDocuments);
    dom.ticketModal.addEventListener('click', (event) => {
      if (event.target === dom.ticketModal) {
        closeModal();
      }
    });
  }

  function readSession() {
    try {
      return JSON.parse(sessionStorage.getItem('cr_session') || 'null');
    } catch (error) {
      return null;
    }
  }

  function normalizeInstitutionUser(session) {
    const role = String(session.user_role || session.role || '').trim().toLowerCase();
    const id = session.user_id || session.id || session.userId || session.profile?.user_id;
    const profile = session.profile || {};
    return {
      id,
      role,
      displayName: session.display_name || profile.institution_name || profile.name || 'Instituție parteneră',
      serviceType: profile.service_type || session.institution_service_type || 'Serviciu operațional',
      initials: getInitials(session.display_name || profile.institution_name || 'Instituție')
    };
  }

  function syncProfileUi() {
    dom.instAvatar.textContent = state.institutionUser.initials;
    dom.institutionName.textContent = state.institutionUser.displayName;
    dom.institutionService.textContent = state.institutionUser.serviceType;
    dom.institutionAssignedChip.textContent = `Instituție: ${state.institutionUser.displayName}`;
  }

  function loadTickets() {
    const raw = readTicketsFromSource();
    state.tickets = raw.filter((ticket) => String(ticket.assigned_institution_user_id) === String(state.institutionUser.id));
    populateLabelFilter(state.tickets);
    updateStats();
    applyFilters();
  }

  function readTicketsFromSource() {
    if (window.MockDB && typeof window.MockDB.getAllTickets === 'function') {
      const dbTickets = window.MockDB.getAllTickets();
      return dbTickets.map(normalizeTicket);
    }

    if (window.MockDB && typeof window.MockDB.getInstitutionTickets === 'function') {
      const dbTickets = window.MockDB.getInstitutionTickets(state.institutionUser.id);
      return dbTickets.map(normalizeTicket);
    }

    return FALLBACK_TICKETS.map(normalizeTicket);
  }

  function normalizeTicket(ticket) {
    return {
      id: ticket.id,
      ticket_number: ticket.ticket_number || ticket.ticketNumber || `TK-${ticket.id}`,
      problem_tag: ticket.problem_tag || ticket.label || 'Nespecificat',
      address: ticket.address || ticket.location || 'Adresă indisponibilă',
      gps_lat: Number(ticket.gps_lat ?? ticket.lat ?? 0),
      gps_long: Number(ticket.gps_long ?? ticket.lng ?? 0),
      description: ticket.description || 'Fără descriere.',
      status: ticket.status || 'Trimis',
      ranking_score: Number(ticket.ranking_score ?? 0),
      is_red_flag: Boolean(ticket.is_red_flag),
      assigned_institution_user_id: ticket.assigned_institution_user_id || ticket.institution_user_id,
      institution_name: ticket.institution_name || state.institutionUser.displayName,
      institution_service_type: ticket.institution_service_type || state.institutionUser.serviceType,
      created_at: ticket.created_at || new Date().toISOString(),
      before_photo_url: ticket.before_photo_url || ticket.beforePhotoUrl || '',
      after_photo_url: ticket.after_photo_url || ticket.afterPhotoUrl || '',
      activity_history: Array.isArray(ticket.activity_history) ? ticket.activity_history : [],
      status_history: Array.isArray(ticket.status_history) ? ticket.status_history : [],
      documents: Array.isArray(ticket.documents) ? ticket.documents : []
    };
  }

  function populateLabelFilter(tickets) {
    const labels = [...new Set(tickets.map((ticket) => ticket.problem_tag))].sort((a, b) => a.localeCompare(b, 'ro'));
    const current = dom.labelFilter.value;
    dom.labelFilter.innerHTML = '<option value="">Toate etichetele</option>' + labels.map((label) => `<option value="${escapeHtml(label)}">${escapeHtml(label)}</option>`).join('');
    dom.labelFilter.value = labels.includes(current) ? current : '';
  }

  function updateStats() {
    const tickets = state.tickets;
    dom.newTicketsCount.textContent = tickets.filter((ticket) => ticket.status === 'Trimis').length;
    dom.workingTicketsCount.textContent = tickets.filter((ticket) => ticket.status === 'In_Lucru').length;
    dom.slaTicketsCount.textContent = tickets.filter((ticket) => ticket.is_red_flag).length;
  }

  function applyFilters() {
    const status = dom.statusFilter.value;
    const label = dom.labelFilter.value;
    const search = dom.searchFilter.value.trim().toLowerCase();
    const sort = dom.sortFilter.value;

    let list = [...state.tickets];

    if (status) {
      list = list.filter((ticket) => ticket.status === status);
    }

    if (label) {
      list = list.filter((ticket) => ticket.problem_tag === label);
    }

    if (search) {
      list = list.filter((ticket) => {
        const haystack = [ticket.ticket_number, ticket.problem_tag, ticket.address, ticket.description].join(' ').toLowerCase();
        return haystack.includes(search);
      });
    }

    list.sort((left, right) => {
      if (sort === 'elapsed') {
        return new Date(left.created_at) - new Date(right.created_at);
      }

      if (sort === 'newest') {
        return new Date(right.created_at) - new Date(left.created_at);
      }

      return right.ranking_score - left.ranking_score || new Date(left.created_at) - new Date(right.created_at);
    });

    state.filteredTickets = list;
    renderTable();
  }

  function renderTable() {
    if (!state.filteredTickets.length) {
      dom.institutionTicketBody.innerHTML = '';
      dom.institutionEmptyState.classList.remove('inst-hidden');
      dom.ticketCountChip.textContent = '0 tichete afișate';
      return;
    }

    dom.institutionEmptyState.classList.add('inst-hidden');
    dom.ticketCountChip.textContent = `${state.filteredTickets.length} tichet${state.filteredTickets.length === 1 ? '' : 'e'} afișate`;

    dom.institutionTicketBody.innerHTML = state.filteredTickets.map((ticket) => {
      const mapsLink = buildMapsLink(ticket);
      const elapsed = formatElapsed(ticket.created_at);
      const statusClass = getStatusClass(ticket.status);
      const priorityCell = ticket.is_red_flag
        ? `<span class="inst-flag">● SLA depășit</span>`
        : `<span class="inst-chip">Scor ${ticket.ranking_score}</span>`;

      return `
        <tr>
          <td><span class="inst-ticket-no">#${escapeHtml(ticket.ticket_number)}</span></td>
          <td>${escapeHtml(ticket.problem_tag)}</td>
          <td>
            <div class="inst-address">
              <span>${escapeHtml(ticket.address)}</span>
              <small><a class="inst-gps-link" href="${mapsLink}" target="_blank" rel="noreferrer">Deschide GPS</a></small>
            </div>
          </td>
          <td>${elapsed}</td>
          <td><span class="status-badge ${statusClass}">${formatStatus(ticket.status)}</span></td>
          <td>${priorityCell}</td>
          <td><button type="button" class="btn btn-secondary inst-manage-btn" data-ticket-id="${ticket.id}">Gestionează</button></td>
        </tr>
      `;
    }).join('');

    dom.institutionTicketBody.querySelectorAll('[data-ticket-id]').forEach((button) => {
      button.addEventListener('click', () => openModal(Number(button.dataset.ticketId)));
    });
  }

  function openModal(ticketId) {
    const ticket = getTicket(ticketId);
    if (!ticket) return;

    state.selectedTicketId = ticketId;
    state.pendingAfterPhoto = ticket.after_photo_url || null;

    dom.modalTicketNo.textContent = `#${ticket.ticket_number}`;
    dom.modalStatusBadge.className = `status-badge ${getStatusClass(ticket.status)}`;
    dom.modalStatusBadge.textContent = formatStatus(ticket.status);
    dom.modalRankingChip.textContent = `Scor ${ticket.ranking_score}`;
    dom.modalHeaderText.textContent = `${ticket.institution_name} · ${ticket.institution_service_type}`;
    dom.modalLabel.textContent = ticket.problem_tag;
    dom.modalAddress.textContent = ticket.address;
    dom.modalGps.textContent = `${ticket.gps_lat.toFixed(5)}, ${ticket.gps_long.toFixed(5)}`;
    dom.modalElapsed.textContent = formatElapsed(ticket.created_at);
    dom.modalDescription.textContent = ticket.description;
    dom.internalNoteInput.value = '';
    dom.refusalReasonInput.value = '';
    dom.refusalBox.classList.remove('visible');
    dom.afterPhotoInput.value = '';

    renderBeforePhoto(ticket.before_photo_url);
    renderAfterPhoto(state.pendingAfterPhoto);
    renderJournal(ticket);
    renderDocuments(ticket);
    renderAuditPreview(ticket);
    syncActionVisibility(ticket);

    dom.ticketModal.classList.add('open');
  }

  function closeModal() {
    dom.ticketModal.classList.remove('open');
    state.selectedTicketId = null;
    state.pendingAfterPhoto = null;
  }

  function renderBeforePhoto(url) {
    if (url) {
      dom.beforePhotoBox.innerHTML = `<span class="inst-photo-label">Before</span><img src="${url}" alt="Fotografie înainte de intervenție" />`;
      return;
    }

    dom.beforePhotoBox.innerHTML = '<span class="inst-photo-label">Before</span><div class="inst-photo-placeholder">Nu există încă o fotografie atașată de cetățean.</div>';
  }

  function renderAfterPhoto(url) {
    if (url) {
      dom.afterPhotoPreview.innerHTML = `<span class="inst-photo-label">After</span><img src="${url}" alt="Fotografie după intervenție" />`;
      dom.resolveTicketBtn.disabled = false;
      return;
    }

    dom.afterPhotoPreview.innerHTML = '<div class="inst-photo-placeholder">Încarcă o imagine pentru a activa finalizarea tichetului.</div>';
    dom.resolveTicketBtn.disabled = true;
  }

  function renderJournal(ticket) {
    const items = ticket.activity_history || [];
    if (!items.length) {
      dom.journalList.innerHTML = '<div class="inst-journal-item">Nu există adnotări interne salvate.</div>';
      return;
    }

    dom.journalList.innerHTML = items.slice().reverse().map((entry) => `
      <div class="inst-journal-item">
        <div class="inst-journal-meta">${formatDateTime(entry.created_at)} · ${escapeHtml(entry.actor || state.institutionUser.displayName)}</div>
        <div>${escapeHtml(entry.note || '')}</div>
      </div>
    `).join('');
  }

  function renderDocuments(ticket) {
    const items = ticket.documents || [];
    if (!items.length) {
      dom.pdfList.innerHTML = '<div class="inst-doc-item">Nu există documente PDF încărcate.</div>';
      return;
    }

    dom.pdfList.innerHTML = items.slice().reverse().map((doc) => `
      <div class="inst-doc-item">
        <div>
          <div>${escapeHtml(doc.name)}</div>
          <div class="inst-doc-meta">${escapeHtml(doc.size_label || 'PDF')} · ${formatDateTime(doc.uploaded_at)} · ${escapeHtml(doc.actor || state.institutionUser.displayName)}</div>
        </div>
      </div>
    `).join('');
  }

  function renderAuditPreview(ticket) {
    const list = [...(ticket.status_history || []), ...state.localLogs.filter((entry) => entry.ticket_id === ticket.id)].slice(-5).reverse();
    if (!list.length) {
      dom.auditPreview.innerHTML = '<div class="inst-log-item">Nu există modificări pregătite pentru audit.</div>';
      return;
    }

    dom.auditPreview.innerHTML = list.map((entry) => `
      <div class="inst-log-item">
        <div class="inst-log-meta">${formatDateTime(entry.timestamp || entry.created_at)} · ${escapeHtml(entry.actor || entry.actor_name || state.institutionUser.displayName)}</div>
        <div><strong>${escapeHtml(entry.action_type || 'STATUS_HISTORY')}</strong></div>
        <div>${escapeHtml(formatAuditMessage(entry))}</div>
      </div>
    `).join('');
  }

  function syncActionVisibility(ticket) {
    const isSent = ticket.status === 'Trimis';
    dom.acceptTicketBtn.disabled = !isSent;
    dom.showRefuseBtn.disabled = !isSent;
    dom.markInWorkBtn.disabled = ticket.status === 'Rezolvat';
    if (ticket.status === 'Rezolvat') {
      dom.resolveTicketBtn.disabled = true;
    } else {
      dom.resolveTicketBtn.disabled = !Boolean(state.pendingAfterPhoto);
    }
  }

  function saveJournalNote() {
    const ticket = getSelectedTicket();
    if (!ticket) return;

    const note = dom.internalNoteInput.value.trim();
    if (!note) {
      showToast('Completează jurnalul de activitate înainte de salvare.', 'error');
      return;
    }

    const entry = {
      note,
      actor: state.institutionUser.displayName,
      created_at: new Date().toISOString()
    };

    ticket.activity_history.push(entry);
    dom.internalNoteInput.value = '';
    renderJournal(ticket);
    recordAudit(ticket, 'NOTE_ADDED', ticket.status, ticket.status, note);
    showToast('Adnotarea internă a fost salvată.', 'success');
  }

  function acceptProject() {
    updateTicketStatus('Preluat', 'Proiectul a fost acceptat de instituție.');
  }

  function refuseProject() {
    const ticket = getSelectedTicket();
    if (!ticket) return;

    const reason = dom.refusalReasonInput.value.trim();
    if (!reason) {
      showToast('Motivul refuzului este obligatoriu.', 'error');
      return;
    }

    recordAudit(ticket, 'PROJECT_REFUSED', ticket.status, ticket.status, reason);
    ticket.activity_history.push({
      note: `Refuz proiect: ${reason}`,
      actor: state.institutionUser.displayName,
      created_at: new Date().toISOString()
    });

    dom.refusalBox.classList.remove('visible');
    dom.refusalReasonInput.value = '';
    renderJournal(ticket);
    renderAuditPreview(ticket);
    showToast('Refuzul a fost înregistrat. Dispatcher-ul trebuie notificat din backend.', 'info');
  }

  function markInWork() {
    updateTicketStatus('In_Lucru', 'Intervenția a intrat în execuție.');
  }

  function resolveTicket() {
    const ticket = getSelectedTicket();
    if (!ticket) return;

    if (!state.pendingAfterPhoto) {
      showToast('Pentru finalizare este obligatorie fotografia After.', 'error');
      return;
    }

    ticket.after_photo_url = state.pendingAfterPhoto;
    updateTicketStatus('Rezolvat', 'Intervenția a fost finalizată și marcată pentru validarea dispecerului.');
  }

  function updateTicketStatus(nextStatus, message) {
    const ticket = getSelectedTicket();
    if (!ticket) return;

    const oldStatus = ticket.status;

    if (oldStatus === nextStatus) {
      showToast('Tichetul este deja în statusul selectat.', 'info');
      return;
    }

    ticket.status = nextStatus;
    ticket.status_history.push(createStatusHistory(nextStatus, oldStatus, state.institutionUser.displayName, message));
    recordAudit(ticket, 'STATUS_CHANGED', oldStatus, nextStatus, message);

    updateStats();
    applyFilters();

    if (state.selectedTicketId === ticket.id) {
      openModal(ticket.id);
    }

    showToast(`Status actualizat: ${formatStatus(nextStatus)}.`, 'success');
  }

  function handleAfterPhotoUpload(event) {
    const file = event.target.files?.[0];
    if (!file) return;

    if (!file.type.startsWith('image/')) {
      showToast('Poți încărca doar imagini pentru fotografia After.', 'error');
      event.target.value = '';
      return;
    }

    const reader = new FileReader();
    reader.onload = () => {
      state.pendingAfterPhoto = reader.result;
      renderAfterPhoto(state.pendingAfterPhoto);

      const ticket = getSelectedTicket();
      if (ticket) {
        recordAudit(ticket, 'AFTER_PHOTO_ATTACHED', ticket.status, ticket.status, `Imagine încărcată: ${file.name}`);
        renderAuditPreview(ticket);
      }

      showToast('Fotografia After a fost încărcată.', 'success');
    };
    reader.readAsDataURL(file);
  }

  function uploadDocuments() {
    const ticket = getSelectedTicket();
    if (!ticket) return;

    const files = Array.from(dom.pdfInput.files || []);
    if (!files.length) {
      showToast('Selectează cel puțin un fișier PDF.', 'error');
      return;
    }

    const invalid = files.find((file) => file.type !== 'application/pdf' && !file.name.toLowerCase().endsWith('.pdf'));
    if (invalid) {
      showToast(`Fișier invalid: ${invalid.name}. Sunt acceptate doar PDF-uri.`, 'error');
      return;
    }

    files.forEach((file) => {
      ticket.documents.push({
        name: file.name,
        size_label: formatFileSize(file.size),
        uploaded_at: new Date().toISOString(),
        actor: state.institutionUser.displayName
      });
      recordAudit(ticket, 'PDF_UPLOADED', ticket.status, ticket.status, `Document încărcat: ${file.name}`);
    });

    dom.pdfInput.value = '';
    renderDocuments(ticket);
    renderAuditPreview(ticket);
    showToast('Documentele au fost încărcate cu succes.', 'success');
  }

  function getTicket(ticketId) {
    return state.tickets.find((ticket) => Number(ticket.id) === Number(ticketId));
  }

  function getSelectedTicket() {
    return getTicket(state.selectedTicketId);
  }

  function recordAudit(ticket, actionType, oldValue, newValue, message) {
    const entry = {
      ticket_id: ticket.id,
      action_type: actionType,
      actor: state.institutionUser.displayName,
      timestamp: new Date().toISOString(),
      old_value: oldValue,
      new_value: newValue,
      message
    };
    state.localLogs.push(entry);
  }

  function refreshData() {
    loadTickets();
    showToast('Dashboard actualizat.', 'success');
  }

  function resetFilters() {
    dom.statusFilter.value = '';
    dom.labelFilter.value = '';
    dom.searchFilter.value = '';
    dom.sortFilter.value = 'ranking';
    applyFilters();
  }

  function logout() {
    sessionStorage.removeItem('cr_session');
    window.location.href = 'index.html';
  }

  function createStatusHistory(newStatus, oldStatus, actor, message) {
    return {
      created_at: new Date().toISOString(),
      timestamp: new Date().toISOString(),
      action_type: 'STATUS_HISTORY',
      actor,
      old_value: oldStatus,
      new_value: newStatus,
      message
    };
  }

  function formatStatus(status) {
    const map = {
      Trimis: 'Trimis',
      Preluat: 'Preluat',
      In_Lucru: 'În lucru',
      Rezolvat: 'Rezolvat'
    };
    return map[status] || status;
  }

  function getStatusClass(status) {
    if (status === 'Trimis') return 'status-sent';
    if (status === 'Preluat') return 'status-dispatcher';
    if (status === 'In_Lucru') return 'status-action';
    if (status === 'Rezolvat') return 'status-resolved';
    return 'status-dispatcher';
  }

  function formatElapsed(createdAt) {
    const diff = Date.now() - new Date(createdAt).getTime();
    const hours = Math.floor(diff / (1000 * 60 * 60));
    const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
    const days = Math.floor(hours / 24);

    if (days > 0) {
      return `${days}z ${hours % 24}h`;
    }

    if (hours > 0) {
      return `${hours}h ${minutes}m`;
    }

    return `${Math.max(1, minutes)}m`;
  }

  function formatDateTime(value) {
    return new Date(value).toLocaleString('ro-RO', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  function buildMapsLink(ticket) {
    if (ticket.gps_lat && ticket.gps_long) {
      return `https://www.google.com/maps?q=${ticket.gps_lat},${ticket.gps_long}`;
    }

    return `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(ticket.address)}`;
  }

  function formatAuditMessage(entry) {
    if (entry.action_type === 'STATUS_HISTORY' || entry.action_type === 'STATUS_CHANGED') {
      return `Status: ${formatStatus(entry.old_value || '—')} → ${formatStatus(entry.new_value || '—')}. ${entry.message || ''}`.trim();
    }

    return `${entry.message || 'Modificare înregistrată.'}`;
  }

  function formatFileSize(bytes) {
    if (bytes < 1024) return `${bytes} B`;
    if (bytes < 1024 * 1024) return `${Math.round(bytes / 1024)} KB`;
    return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
  }

  function getInitials(text) {
    return String(text)
      .split(/\s+/)
      .filter(Boolean)
      .slice(0, 2)
      .map((part) => part[0]?.toUpperCase() || '')
      .join('') || 'IN';
  }

  function escapeHtml(value) {
    return String(value)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#39;');
  }

  function showToast(message, type) {
    const toast = document.createElement('div');
    toast.className = `toast ${type || 'info'}`;
    toast.textContent = message;
    dom.toastContainer.appendChild(toast);
    setTimeout(() => toast.remove(), 3600);
  }
})();