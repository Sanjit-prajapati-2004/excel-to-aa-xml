const output = document.getElementById("xml-output");
const outputTitle = document.getElementById("output-title");
const copyButton = document.getElementById("copy-btn");
const errorModal = document.getElementById("error-modal");
const modalMessage = document.getElementById("modal-message");
const modalCloseButton = document.getElementById("modal-close-x");

document.getElementById("holdings-form").addEventListener("submit", async (event) => {
    event.preventDefault();
    await submitForm(event.currentTarget, "/api/convert/holdings", "Holdings XML");
});

document.getElementById("transactions-form").addEventListener("submit", async (event) => {
    event.preventDefault();
    await submitForm(event.currentTarget, "/api/convert/transactions", "Transactions XML");
});

document.querySelectorAll(".file-picker input[type='file']").forEach((input) => {
    input.addEventListener("change", () => {
        const picker = input.closest(".file-picker");
        const label = picker.querySelector("[data-file-name]");
        const fileName = input.files && input.files.length > 0 ? input.files[0].name : "No file chosen";
        label.textContent = fileName;
        picker.classList.toggle("has-file", fileName !== "No file chosen");
    });
});

document.querySelectorAll("[data-close-modal]").forEach((element) => {
    element.addEventListener("click", closeModal);
});

modalCloseButton.addEventListener("click", closeModal);

document.addEventListener("keydown", (event) => {
    if (event.key === "Escape" && !errorModal.hasAttribute("hidden")) {
        closeModal();
    }
});

copyButton.addEventListener("click", async () => {
    if (!copyButton.dataset.xml) {
        return;
    }

    await navigator.clipboard.writeText(copyButton.dataset.xml);
    const originalText = copyButton.textContent;
    copyButton.textContent = "Copied";
    setTimeout(() => {
        copyButton.textContent = originalText;
    }, 1200);
});

async function submitForm(form, endpoint, title) {
    const formData = new FormData(form);
    const submitButton = form.querySelector("button[type='submit']");
    const previousState = {
        output: output.textContent,
        title: outputTitle.textContent,
        xml: copyButton.dataset.xml || "",
        copyDisabled: copyButton.disabled
    };

    submitButton.disabled = true;
    submitButton.textContent = "Generating...";

    try {
        const response = await fetch(endpoint, {
            method: "POST",
            body: formData
        });

        if (!response.ok) {
            throw new Error(await extractErrorMessage(response));
        }

        const xml = await response.text();
        outputTitle.textContent = title;
        output.textContent = xml;
        copyButton.dataset.xml = xml;
        copyButton.disabled = false;
    } catch (error) {
        restorePreview(previousState);
        showModal(error.message);
    } finally {
        submitButton.disabled = false;
        submitButton.textContent = title.includes("Holdings") ? "Generate Holdings XML" : "Generate Transactions XML";
    }
}

async function extractErrorMessage(response) {
    const contentType = response.headers.get("content-type") || "";

    if (contentType.includes("application/json")) {
        const body = await response.json();
        return body.message || "Failed to generate XML";
    }

    return await response.text();
}

function restorePreview(previousState) {
    output.textContent = previousState.output;
    outputTitle.textContent = previousState.title;
    copyButton.dataset.xml = previousState.xml;
    copyButton.disabled = previousState.copyDisabled;
}

function showModal(message) {
    modalMessage.textContent = message;
    errorModal.hidden = false;
    document.body.classList.add("modal-open");
    modalCloseButton.focus();
}

function closeModal() {
    errorModal.hidden = true;
    document.body.classList.remove("modal-open");
}