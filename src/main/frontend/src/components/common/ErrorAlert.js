import Swal from 'sweetalert2';

export const ErrorAlert = (err) => {
    if (err.response.data.httpStatus === 403) {
        Swal.fire(err.response.data.errorMessage, "", 'warning');
    } else {
        Swal.fire('Sorry! There is something worng with the server', 'Try one more time, or grab a coffee ☕️', 'error');
    }
};