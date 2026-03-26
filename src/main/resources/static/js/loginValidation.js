document.addEventListener("DOMContentLoaded", function () {
    console.log("loginValidation loaded");

    const form = document.querySelector("form");
    const emailInput = document.getElementById("email");
    const passwordInput = document.getElementById("password");

    if (!form || !emailInput || !passwordInput) {
        console.log("Login: formularul sau campurile nu au fost gasite.");
        return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    form.addEventListener("submit", function (e) {
        const errors = [];

        const email = emailInput.value.trim();
        const password = passwordInput.value;

        // email valid
        if (!emailRegex.test(email)) {
            errors.push("Email invalid.");
        }

        // parola valida (minim ceva)
        if (password.length < 1) {
            errors.push("Parola nu poate fi goală.");
        }

        // OPTIONAL (dacă vrei mai strict)
        if (password.length > 0 && password.length < 6) {
            errors.push("Parola trebuie sa aiba minim 6 caractere.");
        }

        if (errors.length > 0) {
            e.preventDefault();
            alert(errors.join("\n"));
        }
    });
});