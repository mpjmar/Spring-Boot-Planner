document.addEventListener('DOMContentLoaded', function () {

	const horaInicioSelect = document.getElementById('horaInicio');
	const horaFinSelect = document.getElementById('horaFin');
	const calendarEl = document.getElementById('calendar'); 

	document.querySelectorAll('form').forEach(form => {
		form.addEventListener('submit', function() {
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
		const id = this.dataset.id;
		mostrarFormularioEdicion(id);
		});
	});

	// BOTONES CANCELAR
	document.querySelectorAll('.btn-cancelar').forEach(btn => {
		btn.addEventListener('click', function() {
		const id = this.dataset.id;
		ocultarFormularioEdicion(id);
		});
	});

	function mostrarFormularioEdicion(id) {
		const form = document.getElementById('form-editar-' + id);
		if (form) {
			form.style.display = 'block';
		}
	}

	function ocultarFormularioEdicion(id) {
		document.getElementById('form-editar-' + id).style.display = 'none';
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
});
