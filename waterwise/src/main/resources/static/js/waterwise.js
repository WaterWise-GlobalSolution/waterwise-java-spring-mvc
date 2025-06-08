// Validações específicas do WaterWise
document.addEventListener('DOMContentLoaded', function() {
    // Validação de coordenadas para região de Mairiporã
    const latInput = document.getElementById('latitude');
    const lngInput = document.getElementById('longitude');

    if (latInput) {
        latInput.addEventListener('blur', function() {
            const lat = parseFloat(this.value);
            if (lat && (lat < -23.4 || lat > -23.2)) {
                showWarning(this, 'Coordenada fora da região de Mairiporã');
            }
        });
    }

    if (lngInput) {
        lngInput.addEventListener('blur', function() {
            const lng = parseFloat(this.value);
            if (lng && (lng < -46.7 || lng > -46.5)) {
                showWarning(this, 'Coordenada fora da região de Mairiporã');
            }
        });
    }
});

function showWarning(element, message) {
    element.classList.add('is-warning');
    // Implementar feedback visual
}