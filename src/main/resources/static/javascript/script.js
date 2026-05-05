document.addEventListener('DOMContentLoaded', function () {

	const horaInicioSelect = document.getElementById('horaInicio');
	const horaFinSelect = document.getElementById('horaFin');
	const calendarEl = document.getElementById('calendar'); 

	document.querySelectorAll('form').forEach(form => {
		form.addEventListener('submit', function() {
			if (form.classList.contains('form-eliminar')) {
				return;
			}

			const botonSubmit = form.querySelector('button[type="submit"]');

			if (botonSubmit) {
				botonSubmit.disabled = true;
				botonSubmit.textContent = 'Enviando...';
			}
		});
	});

	if (horaInicioSelect && horaFinSelect) {
		const opcionesFin = Array.from(horaFinSelect.options);
		horaInicioSelect.addEventListener('change', function () {
			const inicio = this.value;
			opcionesFin.forEach(option => {
				if (!option.value) {
					option.hidden = false;
					return;
				}
				option.hidden = option.value <= inicio;
			});
			horaFinSelect.value = '';
		});
	}

	// BOTONES EDITAR
	document.querySelectorAll('.btn-editar').forEach(btn => {
		btn.addEventListener('click', function() {
			mostrarFormularioEdicion(this);
		});
	});

	// BOTONES CANCELAR
	document.querySelectorAll('.btn-cancelar').forEach(btn => {
		btn.addEventListener('click', function() {
			ocultarFormularioEdicion(this);
		});
	});

	function obtenerFormularioEdicion(trigger) {
		const formId = trigger.dataset.formId || ('form-editar-' + trigger.dataset.id);
		return document.getElementById(formId);
	}

	function mostrarFormularioEdicion(trigger) {
		const form = obtenerFormularioEdicion(trigger);
		if (form) {
			form.hidden = false;
			form.style.display = 'block';
		}
	}

	function ocultarFormularioEdicion(trigger) {
		const form = obtenerFormularioEdicion(trigger);
		if (form) {
			form.hidden = true;
			form.style.display = 'none';
		}
	}

	if (calendarEl) {
        const calendar = new FullCalendar.Calendar(calendarEl, {
            initialView: 'dayGridMonth',
            locale: 'es',
            height: 'auto',
            dateClick: function (info) {
                window.location.href = `/bloquesEstudio/dia?fecha=${info.dateStr}`;
            }
        });
        calendar.render();
    }

	// FORMULARIOS ELIMINAR
	document.querySelectorAll('.form-eliminar').forEach(form => {
		form.addEventListener('submit', function (event) {
			const mensaje = form.dataset.confirm || '¿Eliminar este registro?';
	
			if (!confirm(mensaje)) {
				event.preventDefault();
			}
		});
	});

});

