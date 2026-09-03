document.addEventListener("DOMContentLoaded", function () {

    const cards =
        document.querySelectorAll(".card-hover-source");

    const emptyState =
        document.getElementById("card-info-empty");

    const content =
        document.getElementById("card-info-content");

    const image =
        document.getElementById("info-image");

    const name =
        document.getElementById("info-name");

    const type =
        document.getElementById("info-type");

    const attribute =
        document.getElementById("info-attribute");

    const level =
        document.getElementById("info-level");

    const stats =
        document.getElementById("info-stats");

    const description =
        document.getElementById("info-description");


    cards.forEach(function (card) {

        card.addEventListener(
            "mouseenter",
            function () {

                image.src =
                    card.dataset.image;

                name.textContent =
                    card.dataset.name;

                type.textContent =
                    "Type: " + card.dataset.type;

                attribute.textContent =
                    "Attribute: " + card.dataset.attribute;

                level.textContent =
                    "Level: " + card.dataset.level;

                stats.textContent =
                    "ATK "
                    + card.dataset.attack
                    + " / DEF "
                    + card.dataset.defense;

                description.textContent =
                    card.dataset.description;

                emptyState.style.display =
                    "none";

                content.classList.add(
                    "active"
                );
            }
        );

    });


    const log =
        document.querySelector(".log-content");

    if (log) {

        log.scrollTop =
            log.scrollHeight;
    }

});