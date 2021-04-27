import Swal from 'sweetalert2';

export const WinnerAlert = (player) => {
    Swal.fire({
        title: player + " is the Winner!!",
        text: 'One More Game?',
        icon: "success",
        allowOutsideClick: false
    }).then((result) => {
        if (result.isConfirmed) {
            window.location.reload();
        }
    })
};


