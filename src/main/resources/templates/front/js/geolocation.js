console.log("geolocation.js loaded");

async function getGeolocation() {
    console.log("geolocation button clicked");
  const shouldUseLocation = confirm(
    "Doriți să folosiți datele GPS ale dispozitivului pentru localizarea precisă a problemei?"
  );

  if (!shouldUseLocation) return;

  if (!navigator.geolocation) {
    alert("Geolocation nu este suportat de acest browser.");
    return;
  }

  navigator.geolocation.getCurrentPosition(
    async function (position) {
      const lat = position.coords.latitude.toFixed(6);
      const lon = position.coords.longitude.toFixed(6);

      document.getElementById("gps_lat").value = lat;
      document.getElementById("gps_long").value = lon;

      try {
        const response = await fetch(
          `https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=${lat}&lon=${lon}&addressdetails=1&zoom=18&accept-language=ro`
        );

        if (!response.ok) {
          throw new Error("Nu s-a putut obține adresa.");
        }

        const data = await response.json();
        const address = data.address || {};

        const road =
          address.road ||
          address.pedestrian ||
          address.footway ||
          address.cycleway ||
          address.path ||
          "";

        const houseNumber = address.house_number || "";

        const city =
          address.city ||
          address.town ||
          address.village ||
          address.municipality ||
          "";

        const county =
          address.county ||
          address.state_district ||
          address.state ||
          "";

        const borough = address.suburb || address.city_district || "";
        const isBucharest =
          city.trim().toLowerCase() === "bucurești" ||
          city.trim().toLowerCase() === "bucuresti" ||
          county.trim().toLowerCase().includes("bucure");

        let finalAddress = "";

        if (isBucharest) {
          finalAddress = [
            [road, houseNumber].filter(Boolean).join(" ").trim(),
            borough,
            county || city
          ]
            .filter(Boolean)
            .join(", ");
        } else {
          finalAddress = [
            [road, houseNumber].filter(Boolean).join(" ").trim(),
            city,
            county
          ]
            .filter(Boolean)
            .join(", ");
        }

        if (finalAddress) {
          document.getElementById("address").value = finalAddress;
        } else if (data.display_name) {
          document.getElementById("address").value = data.display_name;
        } else {
          alert("Coordonatele au fost preluate, dar adresa nu a putut fi completată automat.");
        }

        const districtField = document.getElementById("district");

        if (districtField) {
          const normalizedCounty = county.trim().toLowerCase();

          if (normalizedCounty.startsWith("sector ")) {
            districtField.value =
              normalizedCounty.charAt(0).toUpperCase() + normalizedCounty.slice(1);
          } else if (county) {
            districtField.value = "Alt județ";
          }
        }
      } catch (error) {
        alert("Coordonatele au fost preluate, dar completarea automată a adresei nu a reușit.");
      }
    },
    function () {
      alert("Vă rugăm să activați locația din setările dispozitivului și să permiteți accesul aplicației.");
    },
    {
      enableHighAccuracy: true,
      timeout: 10000,
      maximumAge: 0
    }
  );
}