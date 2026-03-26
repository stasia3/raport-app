window.MockDB = (() => {
  const USERS = [
    {
      id: 1,
      email: 'cetatean@civic.ro',
      password_hash: 'demo123',
      user_role: 'Citizen',
      last_login: null
    },
    {
      id: 2,
      email: 'dispatcher@civic.ro',
      password_hash: 'demo123',
      user_role: 'Dispatcher',
      last_login: null
    },
    {
      id: 3,
      email: 'dispatcher.zone@civic.ro',
      password_hash: 'demo123',
      user_role: 'Dispatcher',
      last_login: null
    }
  ];

  const PERSOANA_FIZICA = [
    {
      user_id: 1,
      first_name: 'Ana',
      last_name: 'Popescu',
      phone_number: '0712345678',
      karma_points: 42,
      user_type: 'Identificat'
    }
  ];

  const DISPATCHER = [
    {
      user_id: 2,
      full_name: 'Maria Ionescu',
      role: 'Dispecer',
      access_level: 2,
      assigned_zone: null
    },
    {
      user_id: 3,
      full_name: 'Radu Georgescu',
      role: 'Team_Lead',
      access_level: 4,
      assigned_zone: 'Galați'
    }
  ];

  const POST = [
    {
      id: 101,
      creator_user_id: 1,
      title: 'Groapă mare în carosabil',
      description: 'Groapă adâncă lângă trecerea de pietoni.',
      problem_tag: 'Groapă',
      city: 'Galați',
      street: 'Str. Brăilei',
      gps_latitude: 45.4354,
      gps_longitude: 28.0500,
      created_at: '2026-03-25T10:15:00',
      is_anonymous: false
    },
    {
      id: 102,
      creator_user_id: 1,
      title: 'Iluminat defect',
      description: 'Doi stâlpi nu funcționează noaptea.',
      problem_tag: 'Iluminat',
      city: 'Galați',
      street: 'Str. Domnească',
      gps_latitude: 45.4440,
      gps_longitude: 28.0440,
      created_at: '2026-03-25T08:30:00',
      is_anonymous: false
    },
    {
      id: 103,
      creator_user_id: 1,
      title: 'Canalizare fără capac',
      description: 'Lipsește capacul, pericol pentru pietoni.',
      problem_tag: 'Canalizare',
      city: 'Galați',
      street: 'Str. Tecuci',
      gps_latitude: 45.4420,
      gps_longitude: 28.0480,
      created_at: '2026-03-25T12:00:00',
      is_anonymous: true
    }
//    {
//      user_id: 301,
//      email: 'salubritate@civicreport.ro',
//      password: 'test1234',
//      user_role: 'Institution',
//      display_name: 'Serviciu Salubritate Municipal',
//      profile: {
//        user_id: 301,
//        institution_name: 'Serviciu Salubritate Municipal',
//        service_type: 'Salubritate'
//      }
//    }
  ];

  const TICKET = [
    {
      id: 201,
      ticket_number: 'TK-0001',
      post_id: 101,
      problem_tag: 'Groapă',
      assigned_institution_user_id: null,
      dispatcher_user_id: 3,
      status: 'Sesizat',
      ranking_score: 87,
      last_update: '2026-03-25T12:10:00',
      is_red_flag: true,
      county_sector: 'Galați'
    },
    {
      id: 202,
      ticket_number: 'TK-0002',
      post_id: 102,
      problem_tag: 'Iluminat',
      assigned_institution_user_id: null,
      dispatcher_user_id: 3,
      status: 'Validat',
      ranking_score: 69,
      last_update: '2026-03-25T11:00:00',
      is_red_flag: false,
      county_sector: 'Galați'
    },
    {
      id: 203,
      ticket_number: 'TK-0003',
      post_id: 103,
      problem_tag: 'Canalizare',
      assigned_institution_user_id: null,
      dispatcher_user_id: 2,
      status: 'Sesizat',
      ranking_score: 93,
      last_update: '2026-03-25T12:20:00',
      is_red_flag: true,
      county_sector: 'Galați'
    }
  ];

  const PHOTO = [
    {
      id: 301,
      post_id: 101,
      photo_url: 'https://via.placeholder.com/640x360?text=Groapa',
      photo_type: 'Before'
    },
    {
      id: 302,
      post_id: 102,
      photo_url: 'https://via.placeholder.com/640x360?text=Iluminat',
      photo_type: 'Before'
    }
  ];

  const STATUS_HISTORY = [
    {
      id: 401,
      ticket_id: 201,
      changed_status: 'Sesizat',
      changed_at: '2026-03-25T10:20:00'
    },
    {
      id: 402,
      ticket_id: 202,
      changed_status: 'Validat',
      changed_at: '2026-03-25T09:00:00'
    }
  ];

  const SYSTEM_LOGS = [];

  function clone(data) {
    return JSON.parse(JSON.stringify(data));
  }

  function findUserByEmail(email) {
    return USERS.find(u => u.email.toLowerCase() === String(email).toLowerCase());
  }

  function login(email, password) {
    const user = findUserByEmail(email);

    if (!user || user.password_hash !== password) {
      return { ok: false, message: 'Email sau parolă incorectă.' };
    }

    user.last_login = new Date().toISOString();

    const session = {
      id: user.id,
      user_id: user.id,
      email: user.email,
      user_role: user.user_role,
      last_login: user.last_login
    };

    if (user.user_role === 'Citizen') {
      const profile = PERSOANA_FIZICA.find(p => p.user_id === user.id);
      session.profile = clone(profile);
      session.display_name = `${profile.first_name} ${profile.last_name}`;
      session.user_type = profile.user_type;
    }

    if (user.user_role === 'Dispatcher') {
      const profile = DISPATCHER.find(d => d.user_id === user.id);
      session.profile = clone(profile);
      session.display_name = profile.full_name;
      session.assigned_zone = profile.assigned_zone;
      session.dispatcher_role = profile.role;
      session.access_level = profile.access_level;
    }

    return { ok: true, data: clone(session) };
  }

  function saveDispatcherZone(userId, zone) {
    const dispatcher = DISPATCHER.find(d => d.user_id === Number(userId));
    if (!dispatcher) return null;

    dispatcher.assigned_zone = zone;

    SYSTEM_LOGS.push({
      id: SYSTEM_LOGS.length + 1,
      actor_user_id: Number(userId),
      action_type: 'ZONE_ASSIGNED',
      old_value: null,
      new_value: JSON.stringify({ assigned_zone: zone }),
      timestamp: new Date().toISOString()
    });

    return clone(dispatcher);
  }

  function getCitizenPosts(userId) {
    return clone(POST.filter(p => p.creator_user_id === Number(userId)));
  }

  function getDispatcherTicketsByZone(zone) {
    if (!zone) return [];
    return clone(
      TICKET.filter(t => t.county_sector === zone).map(ticket => {
        const post = POST.find(p => p.id === ticket.post_id);
        const photo = PHOTO.find(ph => ph.post_id === ticket.post_id && ph.photo_type === 'Before');

        return {
          ...ticket,
          street: post?.street || '',
          city: post?.city || '',
          location: `${post?.street || ''}, ${post?.city || ''}`,
          gps_lat: post?.gps_latitude || null,
          gps_long: post?.gps_longitude || null,
          description: post?.description || '',
          created_at: post?.created_at || ticket.last_update,
          photo_url: photo?.photo_url || null
        };
      })
    );
  }

  function getTables() {
    return {
      USERS: clone(USERS),
      PERSOANA_FIZICA: clone(PERSOANA_FIZICA),
      DISPATCHER: clone(DISPATCHER),
      POST: clone(POST),
      TICKET: clone(TICKET),
      PHOTO: clone(PHOTO),
      STATUS_HISTORY: clone(STATUS_HISTORY),
      SYSTEM_LOGS: clone(SYSTEM_LOGS)
    };
  }

  return {
    login,
    saveDispatcherZone,
    getCitizenPosts,
    getDispatcherTicketsByZone,
    getTables
  };
})();