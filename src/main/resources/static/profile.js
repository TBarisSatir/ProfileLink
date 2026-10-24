const profileCard = document.getElementById("profile-card");
const notFound = document.getElementById("not-found");

const profileName = document.getElementById("profile-name");
const profileHeadline = document.getElementById("profile-headline");
const skillsList = document.getElementById("skills-list");

const profileInitial = document.getElementById("profile-initial");
const profileId = document.getElementById("profile-id");

async function loadProfile() {

    const pathParts = window.location.pathname.split("/");
    const id = pathParts[pathParts.length - 1];

    try {

        const response = await fetch(`/api/profiles/${id}`);

        if (response.status === 404) {
            notFound.classList.remove("hidden");
            return;
        }

        if (!response.ok) {
            throw new Error("Could not load profile.");
        }

        const profile = await response.json();

        profileName.textContent = profile.name;
        profileHeadline.textContent = profile.headline;

        profileInitial.textContent =
            profile.name.trim().charAt(0).toUpperCase();

        profileId.textContent = profile.id;

        skillsList.innerHTML = "";

        profile.skills.forEach(skill => {

            const element = document.createElement("span");

            element.className = "skill-chip";
            element.textContent = skill;

            skillsList.appendChild(element);
        });

        profileCard.classList.remove("hidden");

    } catch (error) {

        notFound.classList.remove("hidden");
    }
}

loadProfile();