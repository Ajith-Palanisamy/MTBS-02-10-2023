$(document).ready(function(){
	
	$('#registerForm').submit(function(event){
		event.preventDefault();
		//input field validations
		if (!$('#name').val()) {
            alert('Enter your name');
            $('#name').focus();
        }
		var mobileNumber=$('#registerMobileNumber').val();
		var mobileNumberformat = /^[6-9]\d{9}$/;
		if(!mobileNumber.match(mobileNumberformat))
		{
			alert("Enter valid MobileNumber");
			$('#registerMobileNumber').focus();
			return;
		}
		
		var email=$('#registerEmail').val();
		var mailformat = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/;
		if(!email.match(mailformat))
		{
			alert("Enter valid Email");
			$('#registerEmail').focus();
			return;
		}
		
		var password=$('#registerPassword').val();
		var cpassword=$('#confirmPassword').val();
		if(password!=cpassword)
		{
			alert("Password and Confirm Password does not match");
			$('#registerPassword').focus();
			return;
		}
		
		var data=$(this).serialize();
		console.log(data);
		$.ajax({
			url:'./webapi/user/register',
			type:'POST',
			data:data,
			success:function(res)
			{
				if(res.success)
				{
					alert(res.success);
					$('#loginDiv').show();
					$('#registerDiv').hide();
				}
				else if(res.error)
				{
					alert(res.error);
					$('#registerEmail').focus();
					return;
				}
			},
			error:function(error)
			{
			   console.log("Error occured while registration"+error);
			   alert("Error occured while registration"+error);
			}
		})
	});

	
	$('#registerLink').click(function(event){
		event.preventDefault();
		
		$('#loginDiv').hide();
		$('#registerDiv').show();
		
	});
	
	$('#loginForm').submit(function(event){
		event.preventDefault();
		
		var formData = $(this).serialize();
	    console.log(formData);
	    $.ajax({
	      url: './webapi/user/login',
	      type: 'POST',
	      data: formData,
	      success: function(response) {
			if(response.error)
			{
				alert(response.error);
				return;
			}
			localStorage.setItem('user_id',response.user_id);
			localStorage.setItem('user_name',response.name);
			localStorage.setItem("token",response.token);
			
			
			
			if(response.user_role_id===1)
			window.location.href = "admin.html";
			else if(response.user_role_id===3)
			window.location.href = "manager.html"; 
			else if(response.user_role_id===2)
			window.location.href = "user.html";
	  	  },
      	  error:function(error)
		  {
			console.log("Error occured while getting user details"+error);
			alert("Error occured while getting user details"+error);
		  }
      	  
    });
		
	});
	
	
});

