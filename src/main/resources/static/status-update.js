function hideStatusAction() {
    htmx.find('#status-actions').classList.remove('show');
    htmx.find('#status-overlay').classList.remove('show');
}

function showStatusAction() {
    htmx.find('#status-actions').classList.toggle('show');
    htmx.find('#status-overlay').classList.toggle('show');
}