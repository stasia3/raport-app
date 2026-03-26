document.addEventListener("DOMContentLoaded", function () {
    const input = document.getElementById("images");
    const previewList = document.getElementById("image-preview-list");

    if (!input || !previewList) return;

    input.addEventListener("change", function () {
        previewList.innerHTML = "";

        if (!input.files || input.files.length === 0) {
            previewList.textContent = "Nu ai selectat nicio fotografie.";
            return;
        }

        Array.from(input.files).forEach(file => {
            const item = document.createElement("div");
            item.textContent = `${file.name} (${Math.round(file.size / 1024)} KB)`;
            previewList.appendChild(item);
        });
    });
});