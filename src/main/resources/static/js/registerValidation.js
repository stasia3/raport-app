console.log("registerValidation.js loaded");

document.addEventListener("DOMContentLoaded", () => {
    const form = document.querySelector('form[th\\:action="@{/register}"]') || document.querySelector("form");
    if (!form) return;

    const emailInput = document.getElementById("email");
    const phoneInput = document.getElementById("phone");
    const passwordInput = document.getElementById("password");
    const password2Input = document.getElementById("password2");

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    const phoneRegex = /^(?:\+40|0040|0)(7\d{8})$/;

    form.addEventListener("submit", function (e) {
        const errors = [];

        const email = emailInput?.value.trim() || "";
        const phone = phoneInput?.value.trim() || "";
        const password = passwordInput?.value || "";
        const password2 = password2Input?.value || "";

        if (!emailRegex.test(email)) {
            errors.push("Email invalid.");
        }

        if (phone !== "" && !phoneRegex.test(phone.replace(/\s+/g, ""))) {
            errors.push("Număr de telefon invalid.");
        }

        if (password !== password2) {
            errors.push("Parolele nu coincid.");
        }

        if (errors.length > 0) {
            e.preventDefault();
            alert(errors.join("\n"));
        }
    });
});