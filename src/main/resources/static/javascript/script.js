document.addEventListener('DOMContentLoaded', function () {

	const horaInicioSelect = document.getElementById('horaInicio');
	const horaFinSelect = document.getElementById('horaFin');

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

	/* var calendarEl = document.getElementById('calendar');
	var calendar = new FullCalendar.Calendar(calendarEl, {
		initialView: 'timeGridWeek',
		locale: 'es',
		slotMinTime: "08:00:00",
		slotMaxTime: "22:00:00",
		allDaySlot: false,
		height: "auto",
		events: '/cuadrante/[[${cuadrante.id}]]/bloques-json',
		editable: true,
		selectable: true,
		eventClick: function (info) {
			window.location.href = '/bloquesEstudio/' + info.event.id + '/editar';
		},
		eventDrop: function (info) {
			fetch('/bloquesEstudio/' + info.event.id + '/mover', {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json'
				},
				body: JSON.stringify({
					start: info.event.start.toISOString(),
					end: info.event.end ? info.event.end.toISOString() : null
				})
			}).then(response => {
				if (!response.ok) {
					alert('Error al mover el bloque');
					info.revert();
				}
			});
		},
		select: function (info) {
			const start = info.start;
			const end = info.end;
			const fecha = start.toISOString().substring(0, 10);
			const horaInicio = start.toTimeString().substring(0, 5);
			const horaFin = end.toTimeString().substring(0, 5);
			// Extrae el id del cuadrante de la URL de eventos
			const cuadranteId = calendar.getOption('events').split('/')[2];
			window.location.href = `/bloquesEstudio/nuevo?cuadranteId=${cuadranteId}&fecha=${fecha}&horaInicio=${horaInicio}&horaFin=${horaFin}`;
		}
	});
	calendar.render(); */
});
