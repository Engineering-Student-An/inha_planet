window.addEventListener("load", function() {
    const footer = document.querySelector('.footer');

    function adjustFooter() {
        const contentHeight = document.querySelector('.h-full').offsetHeight;
        const windowHeight = window.innerHeight;

        if (contentHeight < windowHeight) {
            footer.style.position = 'absolute';
            footer.style.bottom = '0';
        } else {
            footer.style.position = 'relative';
        }
    }

    adjustFooter();
    window.addEventListener('resize', adjustFooter);
});
