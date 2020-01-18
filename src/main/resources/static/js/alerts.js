var alerts = {
	deleteConfirm: function (id) {
		Swal.fire({
			title: 'Are you sure?',
			text: "You won't be able to revert this!",
			icon: 'warning',
			showCancelButton: true,
			confirmButtonColor: '#3085d6',
			cancelButtonColor: '#d33',
			confirmButtonText: 'Yes, delete it!'
		}).then((result) => {
			if (result.value) {
				var btn = document.querySelector('a[id="' + id + '"]');
				btn.addEventListener("click", function() {
					window.location.href = "/remove?id=" + id;
				});
				btn.click();
				Swal.fire(
					'Deleted!',
					'Your file has been deleted.',
					'success'
				)
			}
		})
	},
	registerSuccess: function(id) {
		Swal.fire({
			  position: 'center',
			  icon: 'success',
			  title: 'Student with id: ' + id + ' created successfully',
			  showConfirmButton: false,
			  timer: 1500
			})
	}
}