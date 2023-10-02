
var contentStates=[];
$(document).ready(function(){
   
   function pushState()
	{
		 contentStates.push($("#body-content").html())
		 console.log("content pushed.."+contentStates.length);
	}
	
	 $(document).on('click','.transitionLink',function()
	 {
		pushState();
	 }
	 );
	 
	 let user_id=localStorage.getItem("user_id");
	 let user_name=localStorage.getItem("user_name");
     
     	$.ajaxSetup({
    beforeSend: function (xhr) {
        xhr.setRequestHeader('Authorization',localStorage.getItem("token"));
    },
    error:function(err)
      {
		  console.log(err);
		  if(err.status==401)
		  {
			  alert(err.responseText);
			  localStorage.clear();
			  window.location.href = "index.html";
		  }
		  else
		  {
			  alert(err);
		  }
	  }
	});


   $(document).on('click','#movies-link',function(event){
	event.preventDefault();
    	$('#add-form-div').show();
	$('#main-div').show();
	$('#admin-home-div').hide();
	
	refreshDataList();

 })
  // Function to refresh the data list
  function refreshDataList() {
    $.ajax({
      url: './webapi/movie',
      type: 'GET',
      success: function(data) {
        $('#data-list').empty();
  		console.log(data);
  		 let i=1;
  		 var table=$("#movies-table");
		 table.find("tr:not(:first)").remove();
  		 $.each(data, function(index, object) {
			    
			    var row = $("<tr>");
		                row.append($("<td>").text(i));
		                row.append($("<td>").text(object.name));
		                var editButton = $('<button>')
		                .attr("movie",JSON.stringify(object))
			            .text('Edit')
			            .click(editData);
			  			row.append($('<td>').append(editButton));
			          	var deleteButton = $('<button>')
			          	.attr("movie",JSON.stringify(object))
			            .text('Delete')
			            .click(deleteData);
		                row.append($('<td>').append(deleteButton));
		                table.append(row);
	   					i++;
		 });
      }
    });
  }

  // Add data
  $('#add-form').submit(function(event) 
  {
    event.preventDefault();
    
    if(!($("#language1").prop('checked') ||$("#language2").prop('checked') ||$("#language3").prop('checked')))
	{
		alert("Select movie language");
		return false;
	}
    
    var formData = $(this).serialize();
    console.log(formData);
    $.ajax({
      url: './webapi/movie',
      type: 'POST',
      data: formData,
      success: function(data) {
	    alert("Movie added..");
	    clearFormData("#add-form");
        refreshDataList();
      }
    });
  });

  // Populate selected movie data in edit form 
  function editData() {
	$('#edit-form-div').show();
    var movie=JSON.parse($(this).attr("movie"));
    var id = movie.movie_id;
    $('#edit-form-div').show();
    clearFormData("#edit-form");
    $.ajax({
		url:'./webapi/movie/'+id,
		type:'GET',
		success:function(data){
			$('#editname').val(data.name);
			$('#editcertificate').val(data.certificate);
			$('#editdirector').val(data.director);
			$('#editdescription').val(data.description);
			$('#editduration').val(data.duration);
			$('#editimage').val(data.image);
			$('#editsubmit').attr('movie',JSON.stringify(movie));
			var languages=data.languages;
			if(languages.includes("Tamil")){$('#editlanguage1').prop("checked",true);}
			if(languages.includes("English")){$('#editlanguage2').prop("checked",true);}
			if(languages.includes("Hindi")){$('#editlanguage3').prop("checked",true);}
		}
   });
  
}
     // Update data
      $('#edit-form').submit(function(event) {
      event.preventDefault();
      if(!($("#editlanguage1").prop('checked') ||$("#editlanguage2").prop('checked') ||$("#editlanguage3").prop('checked')))
	{
		alert("Select movie language");
		return false;
	}
      var movie = JSON.parse($('#editsubmit').attr('movie'));
      var id=movie.movie_id;
      var formData = $(this).serialize();
      console.log(formData);
	    $.ajax({
	      url: './webapi/movie/'+id,
	      type: 'PUT',
	      data: formData,
	      success: function(data) {
			alert("Movie updated");
			clearFormData("#edit-form");
			  $('#edit-form-div').hide();
	        refreshDataList();
	      }
	    });
	  });

  // Delete data
  function deleteData() {
    var movie=JSON.parse($(this).attr("movie"));
    var id = movie.movie_id;
    console.log(id);
    
    if(confirm('Are you sure want to remove this movie from the application?'))
	{ 

    $.ajax({
      url: './webapi/movie/cancel/' + id,
      type: 'PUT',
      success: function(data) {
	    alert(data);
        refreshDataList();
      }
    });
    }
  }


function clearFormData(formid)
{
	  $(formid+' input[type="text"]').val('');
	  $(formid+' input[type="time"]').val('');
	  $(formid+' select').val('');
	  $(formid+' textarea').val('');
	  $(formid+' input[type="checkbox"]').prop('checked', false);
}
$('input[type=time]').bind("mousewheel", function () {
 return false;
});
   

	$(document).on('click','#offers-link',function(event){
	event.preventDefault();
	$('#add-offer-div').show();
	$('#offers-div').show();
	$('#admin-home-div').hide();
	var now=new Date();
	var today = now.toISOString().split('T')[0];
	$('#start-date').attr('min',today);
	$('#end-date').attr('min',today);
	refreshOffers();
	
   });
   
   $('#start-date').on('change',function(event)
 	{ 
		event.preventDefault();
		start_date=$('#start-date').val();
		$('#end-date').attr('min',start_date);
		
	});
	
	function refreshOffers()
	{
		$.ajax({
        url: './webapi/offer',
        type: 'GET',
        success: function(data) {
                    var table = $("#offers-table");
		            table.find("tr:not(:first)").remove();
		            var i=1;
		            console.log(data);
		             var dataArray = JSON.parse(data);
		             $.each(dataArray,function(index,object)
		             {
		                var row = $("<tr>");
		                row.append($("<td>").text(i));
		                row.append($("<td>").text(object.offer_name));
		                row.append($("<td>").text(object.no_of_tickets));
		                row.append($("<td>").text(object.discount));
		                row.append($("<td>").text(object.start_date));
		                row.append($("<td>").text(object.end_date));
		                
		                var t=new Date(object.end_date);
		                var now=new Date();
		                   
		                var l=$("<button>")
		                .attr("offer",JSON.stringify(object))
		                .attr("href","javascript:void(0)")
		                .attr("class","offerEditLink")
		                .text("Edit");
		                 var td=$("<td>").append(l);
		                 row.append(td);
		                 
		                 l=$("<button>")
		                .attr("offer",JSON.stringify(object))
		                .attr("href","javascript:void(0)")
		                .attr("class","offerRemoveLink")
		                .text("Delete");
		                 td=$("<td>").append(l);
		                 row.append(td);
		                 
		                 table.append(row);
		                 i++;
		                 
      					});
     				
				}
	      });
	}
   
   $('#add-offer-form').submit(function(event){ 
	   event.preventDefault();
		var formDataAsJsonArray = $('#add-offer-form').serializeArray();
		console.log(formDataAsJsonArray);
	    var jsonObject={};
	    $(formDataAsJsonArray).each(function(index,field) {
	          jsonObject[field.name] = field.value;
    	});
    	
    	$.ajax({
       url: './webapi/offer/',
       type: 'POST',
       data: JSON.stringify(jsonObject),
       success: function(data) 
       {
		 alert("Offer added!!");	
		 refreshOffers();
		 clearOfferFormData('#add-offer-form');
	    }
   });
   	});
   	
   $(document).on('click','.offerEditLink',function(event){
		event.preventDefault();
		$('#edit-offer-div').show();
		var offer=JSON.parse($(this).attr("offer"));
		$('#edit-offer-name').val(offer.offer_name);
		$('#edit-offer-name').attr("offer",JSON.stringify(offer));
		$('#edit-no-of-tickets').val(offer.no_of_tickets);
		$('#edit-discount').val(offer.discount);
		$('#edit-start-date').val(offer.start_date);
		$('#edit-end-date').val(offer.end_date);
		
   });
   
   $('#edit-start-date').on('change',function(event)
 	{ 
		event.preventDefault();
		start_date=$('#edit-start-date').val();
		$('#edit-end-date').attr('min',start_date);		
		$('#edit-end-date').val(start_date);
		
	});
   
   
   $('#edit-offer-form').submit(function(event)
   {
	event.preventDefault();
	start_date=$('#edit-start-date').val();
	end_date=$('#edit-end-date').val();
	var startDate=new Date(start_date);
	var endDate=new Date(end_date);
	var now=new Date();
	if(startDate<now && endDate<now)
	{
		alert("Enter a Valid offer time period..");
		return;
	}
	    var formDataAsJsonArray = $('#edit-offer-form').serializeArray();
		console.log(formDataAsJsonArray);
	    var jsonObject={};
	    $(formDataAsJsonArray).each(function(index,field) {
	          jsonObject[field.name] = field.value;
    	});
    	var offer=JSON.parse($('#edit-offer-name').attr("offer"));
    	var offer_id=offer.offer_id;
    	$.ajax({
       url: './webapi/offer/'+offer_id,
       type: 'PUT',
       data: JSON.stringify(jsonObject),
       success: function(data) 
       {
		 alert(data);	
		 refreshOffers();
		 clearOfferFormData('#edit-offer-form');
		 $('#edit-offer-div').hide();
		 
	    }
	
   });
   
   });
   
   $(document).on('click','.offerRemoveLink',function(event){
		event.preventDefault();
		if(confirm('Are you sure? you want to remove this offer ?'))
		{
		var offer=JSON.parse($(this).attr("offer"));
    	var offer_id=offer.offer_id;
        $.ajax({
       url: './webapi/offer/'+offer_id+'/cancel',
       type: 'PUT',
       success: function(data) 
       {
		 alert(data);		
		 refreshOffers()
	    }
	     });
	     }
		
	 });
   
   function clearOfferFormData(formid)
	{
	   $(formid+' input[type="text"]').val('');
	   $(formid+' input[type="date"]').val('');
	   $(formid+' input[type="number"]').val('');
	 }


$(document).on('click','#theaters-link',function(event){
	event.preventDefault();
	$('#add-theater-div').show();
	$('#theaters-div').show();
	$('#admin-home-div').hide();
	
	refreshTheaters();
   });
   
   function refreshTheaters()
	{
		$.ajax({
        url: './webapi/theater',
        type: 'GET',
        success: function(data) {
                    var table = $("#theaters-table");
                    //alert(data);
                    console.log(data);
		            table.find("tr:not(:first)").remove();
		            var i=1;
		            
		             if(data.length>0)
		             {
			             $.each(data,function(index,object)
			             {
			                var row = $("<tr>");
			                row.append($("<td>").text(i));
			                row.append($("<td>").text(object.theater_name));
			                row.append($("<td>").text(object.manager_email));
			                row.append($("<td>").text(object.district));
			                
			                var l=$("<button>")
			                .attr("theater",JSON.stringify(object))
			                .attr("href","javascript:void(0)")
			                .attr("class","theaterEditLink")
			                .text("Edit");
			                 var td=$("<td>").append(l);
			                 row.append(td);
			                 
			                 l=$("<button>")
			                .attr("theater",JSON.stringify(object))
			                .attr("href","javascript:void(0)")
			                .attr("class","theaterRemoveLink")
			                .text("Remove");
			                 td=$("<td>").append(l);
			                 row.append(td);
			                 
			                 table.append(row);
			                 i++;
			                 
	      					});
	      				}
     				
				}
	      });
	}
   
   $('#add-theater-form').submit(function(event){ 
	   event.preventDefault();
		var formDataAsJsonArray = $('#add-theater-form').serializeArray();
		console.log(formDataAsJsonArray);
	    var jsonObject={};
	    $(formDataAsJsonArray).each(function(index,field) {
	          jsonObject[field.name] = field.value;
    	});
    	
    	$.ajax({
       url: './webapi/theater/',
       type: 'POST',
       data: JSON.stringify(jsonObject),
       success: function(data) 
       {
		 alert(data);
		 clearTheaterFormData('#add-theater-form');	
		 refreshTheaters();
	    }
   });
   	});
   
   
   
   $(document).on('click','.theaterEditLink',function(event){
		event.preventDefault();
		var theater=JSON.parse($(this).attr("theater"));
		$('#edit-theater-div').show();
		$('#edit-theater-name').val(theater.theater_name);
		$('#edit-theater-manager').val(theater.manager_email);
		$('#edit-theater-district').val(theater.district);
		$('#edit-theater-button').attr("theater",JSON.stringify(theater));
   });
   
   $('#edit-theater-form').submit(function(event){
	event.preventDefault();
	var formDataAsJsonArray = $('#edit-theater-form').serializeArray();
		console.log(formDataAsJsonArray);
	    var jsonObject={};
	    $(formDataAsJsonArray).each(function(index,field) {
	          jsonObject[field.name] = field.value;
    	});
    	var theater=JSON.parse($('#edit-theater-button').attr("theater"));
    	var theater_id=theater.theater_id;
    	$.ajax({
       url: './webapi/theater/'+theater_id,
       type: 'PUT',
       data: JSON.stringify(jsonObject),
       success: function(data) 
       {
		 alert(data);
		 clearTheaterFormData('#edit-theater-form');
		 $('#edit-theater-div').hide();	
		 refreshTheaters();
	    }
   });

	});
	
	function clearTheaterFormData(formid)
	{
	   $(formid+' input[type="text"]').val('');
	   $(formid+' input[type="email"]').val('');
	 }
	 
	 
	 
	 $(document).on('click','.theaterRemoveLink',function(event)
	 {
		event.preventDefault();
		if(confirm('Are you sure? you want to remove this theater from the Application'))
		{
	    	var theater=JSON.parse($(this).attr("theater"));
    	    var theater_id=theater.theater_id;
	        $.ajax({
			       url: './webapi/theater/'+theater_id+'/cancel',
			       type: 'PUT',
			       success: function(data) 
			       {
					 alert(data);	
					 refreshTheaters();
				    }
		      });
	      }
		
	  });
	  
	  
	  $(document).on('click','#backButton',function() {
	   //alert("Back button pressed");
	   
	   console.log(contentStates);
       if (contentStates.length > 0) {
       var previousState = contentStates.pop();
       $("#body-content").html(previousState);
       console.log('content poped..'+contentStates.length);
      }
  	 });
  	 $(document).on('click','#log-out-link',function()
	 {
		 localStorage.clear();
		 window.location.href = "index.html";
	 }
	 );

 });

