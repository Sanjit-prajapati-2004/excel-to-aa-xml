const output = document.getElementById("xml-output");
const outputTitle = document.getElementById("output-title");
const copyButton = document.getElementById("copy-btn");

document.getElementById("holdings-form").addEventListener("submit", async (event) => {
    event.preventDefault();
    await submitForm(event.currentTarget, "/api/convert/holdings", "Holdings XML");
});

document.getElementById("transactions-form").addEventListener("submit", async (event) => {
    event.preventDefault();
    await submitForm(event.currentTarget, "/api/convert/transactions", "Transactions XML");
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
    const button = form.querySelector("button[type='submit']");
    button.disabled = true;
    button.textContent = "Generating...";
    output.textContent = "Generating XML...";

    try {
        const response = await fetch(endpoint, {
            method: "POST",
            body: formData
        });

        if (!response.ok) {
            throw new Error(await response.text() || "Failed to generate XML");
        }

        const xml = await response.text();
        outputTitle.textContent = title;
        output.textContent = xml;
        copyButton.dataset.xml = xml;
        copyButton.disabled = false;
    } catch (error) {
        outputTitle.textContent = "Error";
        output.textContent = error.message;
        copyButton.dataset.xml = "";
        copyButton.disabled = true;
    } finally {
        button.disabled = false;
        button.textContent = title.includes("Holdings") ? "Generate Holdings XML" : "Generate Transactions XML";
    }
}
