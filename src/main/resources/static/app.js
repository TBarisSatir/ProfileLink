const form = document.getElementById("profile-form");
const result = document.getElementById("result");
const shareLinkInput = document.getElementById("share-link");
const viewProfileLink = document.getElementById("view-profile");
const copyButton = document.getElementById("copy-button");
const errorMessage = document.getElementById("error-message");

const submitButton = document.getElementById("submit-button");
const submitText = document.getElementById("submit-text");

form.addEventListener("submit", async (event) => {
    event.preventDefault();

    result.classList.add("hidden");
    errorMessage.classList.add("hidden");

    const name = document.getElementById("name").value.trim();
    const headline = document.getElementById("headline").value.trim();

    const skills = document
        .getElementById("skills")
        .value
        .split(",")
        .map(skill => skill.trim())
        .filter(skill => skill.length > 0);

    if (!name || !headline || skills.length === 0) {
        errorMessage.textContent =
            "Name, headline and at least one skill are required.";

        errorMessage.classList.remove("hidden");
        return;
    }

    submitButton.disabled = true;
    submitText.textContent = "Generating...";

    try {
        const response = await fetch("/api/profiles", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                name,
                headline,
                skills
            })
        });

        if (!response.ok) {
            throw new Error("Could not create profile. Please try again.");
        }

        const profile = await response.json();

        const shareUrl =
            `${window.location.origin}/p/${profile.id}`;

        shareLinkInput.value = shareUrl;
        viewProfileLink.href = shareUrl;

        result.classList.remove("hidden");

    } catch (error) {
        errorMessage.textContent = error.message;
        errorMessage.classList.remove("hidden");

    } finally {
        submitButton.disabled = false;
        submitText.textContent = "Generate profile";
    }
});

copyButton.addEventListener("click", async () => {
    try {
        await navigator.clipboard.writeText(shareLinkInput.value);

        copyButton.textContent = "Copied";

        setTimeout(() => {
            copyButton.textContent = "Copy";
        }, 1500);

    } catch {
        copyButton.textContent = "Failed";

        setTimeout(() => {
            copyButton.textContent = "Copy";
        }, 1500);
    }
});