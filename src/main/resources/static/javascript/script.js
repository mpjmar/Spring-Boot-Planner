document.addEventListener('DOMContentLoaded', function() {
  var calendarEl = document.getElementById('calendar');
  var calendar = new FullCalendar.Calendar(calendarEl, {
    initialView: 'timeGridWeek',
    locale: 'es',
    slotMinTime: "08:00:00",
    slotMaxTime: "22:00:00",
    allDaySlot: false,
    height: "auto",
    events: '/cuadrante/[[${cuadrante.id}]]/bloques-json',
    editable: true,
    eventClick: function(info) {
      window.location.href = '/bloquesEstudio/' + info.event.id + '/editar';
    },
    eventDrop: function(info) {
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
    }
  });
  calendar.render();
});