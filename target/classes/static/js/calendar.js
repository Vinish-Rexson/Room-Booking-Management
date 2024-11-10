document.addEventListener('DOMContentLoaded', function() {
    let calendar = new FullCalendar.Calendar(document.getElementById('calendar'), {
        initialView: 'dayGridMonth',
        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: 'dayGridMonth,dayGridWeek'
        },
        events: function(fetchInfo, successCallback, failureCallback) {
            const roomFilter = document.getElementById('roomFilter').value;
            
            fetch(`/api/calendar/availability?start=${fetchInfo.startStr}&end=${fetchInfo.endStr}&roomId=${roomFilter}`)
                .then(response => response.json())
                .then(data => {
                    const events = data.map(booking => ({
                        title: `${booking.roomName} - ${booking.customerName || 'Available'}`,
                        start: booking.checkInDate,
                        end: booking.checkOutDate,
                        className: booking.available ? 'booking-available' : 'booking-occupied',
                        extendedProps: {
                            roomId: booking.roomId,
                            bookingId: booking.id,
                            customerName: booking.customerName
                        }
                    }));
                    successCallback(events);
                })
                .catch(error => {
                    console.error('Error fetching events:', error);
                    failureCallback(error);
                });
        }
    });
    calendar.render();

    // Populate room filter
    fetch('/api/manage/rooms')
        .then(response => response.json())
        .then(rooms => {
            const roomFilter = document.getElementById('roomFilter');
            rooms.forEach(room => {
                const option = document.createElement('option');
                option.value = room.id;
                option.textContent = room.name;
                roomFilter.appendChild(option);
            });
        });

    // Add event listener for room filter
    document.getElementById('roomFilter').addEventListener('change', function() {
        calendar.refetchEvents();
    });
}); 