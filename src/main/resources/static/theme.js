
(() => {
    const root = document.documentElement;
    const storageKey = "javaquest-theme";

    function applyTheme(theme) {
        root.dataset.theme = theme;

        document.querySelectorAll(".theme-toggle").forEach(toggle => {
            const isDark = theme === "dark";
            toggle.setAttribute("aria-pressed", String(isDark));
            toggle.setAttribute(
                "aria-label",
                isDark ? "Switch to light mode" : "Switch to dark mode"
            );

            const icon = toggle.querySelector(".toggle-icon");
            if (icon) icon.textContent = isDark ? "☾" : "☀";
        });
    }

    let theme = "dark";

    try {
        theme = localStorage.getItem(storageKey) || "dark";
    } catch (error) {}

    applyTheme(theme === "light" ? "light" : "dark");

    document.addEventListener("click", event => {
        const target = event.target;
        const toggle = target && typeof target.closest === "function"
            ? target.closest(".theme-toggle") : null;
        if (!toggle) return;

        event.preventDefault();
        const nextTheme = root.dataset.theme === "dark" ? "light" : "dark";
        applyTheme(nextTheme);
        try { localStorage.setItem(storageKey, nextTheme); } catch (error) {}
    });
})();
