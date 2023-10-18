function getCorrectBaseUrl() {
    const currentUrl = new URL(window.location.href);
    // Get the current path segments
    const pathSegments = currentUrl.pathname.split('/').filter(segment => segment !== '');

    // Remove the last segment of the current path
    pathSegments.pop();

    // Combine the path segments into a base path string
    let base = '';
    if (pathSegments.length > 0) {
        base = '/' + pathSegments.join('/');
    }
    return base;
}