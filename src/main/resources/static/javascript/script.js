document.addEventListener('DOMContentLoaded', function () {

	const horaInicioSelect = document.getElementById('horaInicio');
	const horaFinSelect = document.getElementById('horaFin');
	const calendarEl = document.getElementById('calendar'); 
	const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
	const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

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

	// DRAG & DROP BLOQUES DE ESTUDIO
	const bloquesDraggables = document.querySelectorAll('.bloque-card[draggable="true"]');
	const columnasDrop = document.querySelectorAll('.bloques-semana-columna[data-drop-date]');
	let bloqueArrastrado = null;

	bloquesDraggables.forEach(card => {
		card.addEventListener('dragstart', event => {
			bloqueArrastrado = card;
			card.classList.add('is-dragging');
			event.dataTransfer.effectAllowed = 'move';
			event.dataTransfer.setData('text/plain', card.dataset.bloqueId || '');
		});

		card.addEventListener('dragend', () => {
			card.classList.remove('is-dragging');
			columnasDrop.forEach(col => col.classList.remove('drop-active'));
			bloqueArrastrado = null;
		});
	});

	columnasDrop.forEach(columna => {
		columna.addEventListener('dragover', event => {
			if (!bloqueArrastrado) {
				return;
			}
			event.preventDefault();
			event.dataTransfer.dropEffect = 'move';
			columna.classList.add('drop-active');
		});

		columna.addEventListener('dragleave', () => {
			columna.classList.remove('drop-active');
		});

		columna.addEventListener('drop', async event => {
			event.preventDefault();
			columna.classList.remove('drop-active');

			if (!bloqueArrastrado) {
				return;
			}

			const bloqueId = bloqueArrastrado.dataset.bloqueId;
			const fechaDestino = columna.dataset.dropDate;
			const fechaActual = bloqueArrastrado.dataset.currentDate;

			if (!bloqueId || !fechaDestino || fechaDestino === fechaActual) {
				return;
			}

			const headers = {
				'Content-Type': 'application/json'
			};
			if (csrfToken && csrfHeader) {
				headers[csrfHeader] = csrfToken;
			}

			try {
				const response = await fetch(`/bloquesEstudio/${bloqueId}/mover`, {
					method: 'POST',
					headers,
					body: JSON.stringify({ fecha: fechaDestino })
				});

				if (!response.ok) {
					alert('No se pudo mover el bloque. Revisa solapamientos o permisos.');
					return;
				}

				window.location.reload();
			} catch (error) {
				alert('Error de red al mover el bloque.');
			}
		});
	});

});

