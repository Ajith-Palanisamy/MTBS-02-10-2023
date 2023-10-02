
var contentStates=[];
$(document).ready(function() {

	var theater_name;
	var theater_id;
	var screen_name;
	var screen_id;
	var total_seats;
	var remaining_rows;
	var total_rows;
	var total_columns;
	var user_id,user_name;
	
	var rowsPerPage = 10;
    var currentPage = 1;
    var shows;
	
	function pushState()
	{
		 contentStates.push($("#body-content").html())
		 //console.log($("#body-content").html());
		 console.log("content pushed.."+contentStates.length);
	}
	
	 $(document).on('click','.transitionLink',function()
	 {
		pushState();
	 }
	 );
	user_id=localStorage.getItem("user_id");
	user_name=localStorage.getItem("user_name");
	$('#managers').hide();
	$('#screens').hide();
	$('#theaters').show();
	
	
	
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
	
	getTheaterList(user_id);
	
	function getTheaterList(user_id) {
    $.ajax({
      url: './webapi/user/'+user_id+'/theater',
      type: 'GET',
      success: function(data) {
        $('#theater-list').empty();
  		
  		
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
			                
			                var l=$("<a>")
			                .attr("theater",JSON.stringify(object))
			                .attr("theater_id",object.theater_id)
			                .attr("href","javascript:void(0)")
			                .attr("class","theaterLink transitionLink")
			                .text(object.theater_name);
			                 var td=$("<td>").append(l);
			                 row.append(td);
			                
			                
			                row.append($("<td>").text(object.district));
			                
			                 var l=$("<button>")
				                .attr("theater",JSON.stringify(object))
				                .attr("href","javascript:void(0)")
				                .attr("class","theaterCollectionLink")
				                .text("Collection");
				                 td=$("<td>").append(l);
				                 row.append(td);
			                
			                var l=$("<button>")
			                .attr("theater",JSON.stringify(object))
			                .attr("href","javascript:void(0)")
			                .attr("class","theaterEditLink")
			                .text("Edit");
			                 var td=$("<td>").append(l);
			                 row.append(td);
			                 
			                 var l=$("<button>")
			                .attr("theater",JSON.stringify(object))
			                .attr("href","javascript:void(0)")
			                .attr("class","theaterRemoveLink")
			                .text("Remove");
			                 var td=$("<td>").append(l);
			                 row.append(td);
			                 
			                 table.append(row);
			                 i++;
			                 
	      					});
	      				}
  		
      }
    });
  }
  
  $(document).on('click','.theaterLink',function(){
	$('#theaters').hide();
	$('#screens').show();
	$('#theater-collection').hide();
	$('#screen-collection').hide();
	theater_name=$(this).text();
	$('#screens h1').text(theater_name);
	var theater=JSON.parse($(this).attr("theater"));
	theater_id=theater.theater_id;
	
	localStorage.setItem('theater_id', theater_id);
	localStorage.setItem('theater_name', theater_name);
	//console.log("Theater id : "+theater_id);
	getScreenList(theater_id);
});

$(document).on('click','.theaterEditLink',function(event){
		event.preventDefault();
		var theater=JSON.parse($(this).attr("theater"));
		 $('#screen-collection').hide();
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
    	jsonObject["manager_id"] =user_id;
    	var theater=JSON.parse($('#edit-theater-button').attr("theater"));
    	var theater_id=theater.theater_id;
    	$.ajax({
       url: './webapi/theater/'+theater_id,
       type: 'PUT',
       data: JSON.stringify(jsonObject),
       success: function(data) 
       {
		 alert(data);
		 $('#edit-theater-div').hide();	
		 $('#screen-collection').hide();
		 getTheaterList(user_id);
	    }
   });

	});

$(document).on('click','.theaterCollectionLink',function()
{
	$('#add-screen-form').hide();
	$('#edit-screen-form').hide();
	$('#theater-collection').show();
	window.scrollTo(0, 0);
	
	theater_id=JSON.parse($(this).attr("theater")).theater_id;
	theater_name=JSON.parse($(this).attr("theater")).theater_name;
	
	$('#theater-collection h2').text(theater_name+' '+'collection');
	
	
	
	$.ajax({
		url:'./webapi/theater/'+theater_id+'/collection',
		type:'GET',
		success:function(data)
		{
					var table=$('#theater-collection-table');
					table.find("tr:not(:first)").remove();
		            var collection=0;
		            var i=1;
		            $.each(data, function(index, object) {
		                var row = $("<tr>");
		                row.append($("<td>").text(i));
		                row.append($("<td>").text(object.screen_name));
		                row.append($("<td>").text(object.collection));
		                var t=parseInt(object.collection);
		                collection=collection+t;
		                table.append(row);
		                });
		               var row=$("<tr>");
		               var closeButton = $('<button>')
            			.text('close')
            			.attr('id','theaterCollectionCloseButton');
            		    row.append($('<td>').append(closeButton));
            		    
		                row.append($("<td>").text("Total Collection"));
		                row.append($("<td>").text(collection));
		                table.append(row);       
		}
		
	});
	
});

$(document).on('click','#theaterCollectionCloseButton',function(event)
{
	event.preventDefault();
	$('#theater-collection').hide();
});



 
	 $(document).on('click','.theaterRemoveLink',function(event){
		event.preventDefault();
		user_id=localStorage.getItem("user_id");
		if(confirm('Are you sure? you want to remove this theater'))
		{
    	var theater_id=JSON.parse($(this).attr("theater")).theater_id;
        $.ajax({
       url: './webapi/theater/'+theater_id+'/cancel',
       type: 'PUT',
       success: function(data) 
       {
		 alert(data);	
		 getTheaterList(user_id);
	    }
	     });
	     }
		
	 });

function getScreenList(theater_id)
{
	var theater_name=localStorage.getItem("theater_name");
	$.ajax({
		url:'./webapi/theater/'+theater_id+'/screen',
		type:'GET',
		success:function(data)
		{
			
			$('#screens h1').text(theater_name);
  			
  		
          
                    var table = $("#screens-table");
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
			                
			                var l=$("<a>")
			                .attr("screen",JSON.stringify(object))
			                .attr("href","javascript:void(0)")
			                .attr("class","screenLink transitionLink")
			                .text(object.screen_name);
			                 var td=$("<td>").append(l);
			                 row.append(td);
			                 
			                 var l=$("<button>")
			                 .attr("screen",JSON.stringify(object))
			                .attr("href","javascript:void(0)")
			                .attr("class","screenCollectionLink")
			                .text("View collection");
			                 var td=$("<td>").append(l);
			                 row.append(td);
			                
			                 var l=$("<button>")
			                 .attr("screen",JSON.stringify(object))
			                .attr("href","javascript:void(0)")
			                .attr("class","screenEditLink")
			                .text("Edit Name");
			                 var td=$("<td>").append(l);
			                 row.append(td);
			                
			                 var l=$("<button>")
			                 .attr("screen",JSON.stringify(object))
			                .attr("href","javascript:void(0)")
			                .attr("class","screenRemoveLink")
			                .text("Remove");
			                 var td=$("<td>").append(l);
			                 row.append(td);
			                 
			                 table.append(row);
			                 i++;
			                 
	      					});
	      				}
		}
	})
}

$('#add-screen-form').submit(function(event){
	event.preventDefault();
	
	var screen_name=$('#screen-name').val();
	var theater_id=localStorage.getItem("theater_id");
	var data={
		screen_name:screen_name,
		theater_id:theater_id
	};
	$.ajax({
		url:'./webapi/screen',
		type:'POST',
		data:JSON.stringify(data),
		success:function(response)
		{
			alert(response);
			getScreenList(theater_id);
			$('#screen-name').val('');
			
		}
	})
	
});

$(document).on('click','.screenCollectionLink',function()
{
	$('#add-screen-form').hide();
	$('#edit-screen-form').hide();
	$('#screen-collection').show();
	
	screen_id=JSON.parse($(this).attr("screen")).screen_id;
	screen_name=JSON.parse($(this).attr("screen")).screen_name;
	
	$('#screen-collection h2').text(screen_name+' '+'collection');
	
	localStorage.setItem('screen_id', screen_id);
	localStorage.setItem('screen_name', screen_name);
	
	$.ajax({
		url:'./webapi/screen/'+screen_id+'/collection',
		type:'GET',
		success:function(data)
		{
					var table=$('#screen-collection-table');
					table.find("tr:not(:first)").remove();
		            var collection=0;
		            var i=1;
		            $.each(data, function(index, object) {
		                var row = $("<tr>");
		                row.append($("<td>").text(i));
		                row.append($("<td>").text(object.show_date));
		                row.append($("<td>").text(object.start_time));
		                row.append($("<td>").text(object.movie_name));
		                row.append($("<td>").text(object.language));
		                row.append($("<td>").text(object.show_status));
		                row.append($("<td>").text(object.collection));
		                var t=parseInt(object.collection);
		                collection=collection+t;
		                table.append(row);
		                });
		               var row=$("<tr>");
		               var closeButton = $('<button>')
            			.text('close')
            			.attr('id','screenCollectionCloseButton');
            		    row.append($('<td>').append(closeButton));
            		    
		                row.append($("<td colspan='5'>").text("Total Collection"));
		                row.append($("<td>").text(collection));
		                table.append(row);       
		}
		
	});
	
});

$(document).on('click','#screenCollectionCloseButton',function()
{
	$('#screen-collection').hide();
	$('#add-screen-form').show();
});

$(document).on('click','.screenEditLink',function()
{
	$('#add-screen-form').hide();
	$('#screen-collection').hide();
	$('#edit-screen-form').show();
	screen_id=JSON.parse($(this).attr("screen")).screen_id;
	screen_name=JSON.parse($(this).attr("screen")).screen_name;
	
	localStorage.setItem('screen_id', screen_id);
	localStorage.setItem('screen_name', screen_name);
	
	$('#edit-screen-name').val(screen_name);
});


	
	
$('#edit-screen-form').submit(function(event){	
	event.preventDefault();
	var screen_name=$('#edit-screen-name').val();
	var data={screen_name:screen_name};
	//console.log("Theater id : "+theater_id);
	
	$.ajax({
		  url: './webapi/screen/'+screen_id,
	      type: 'PUT',
	      data:JSON.stringify(data),
	      success: function(response) 
	      {
		    alert(response);
			getScreenList(theater_id);
			$('#edit-screen-name').val('');
			$('#add-screen-form').show();
			$('#edit-screen-form').hide();
	      }
	    });
});

$(document).on('click','.screenRemoveLink',function(){
	
	screen_id=JSON.parse($(this).attr("screen")).screen_id;
	if(confirm("Are you sure want to Delete the Screen?"))
	{
	$.ajax({
		  url: './webapi/screen/'+screen_id+'/cancel',
	      type: 'PUT',
	      success: function(response) 
	      {
		    alert(response);
			getScreenList(theater_id);
	      }
	    });
	    }
});


 $(document).on('click','.screenLink',function(){
	$('#add-screen-form').hide();
	$('#screen-collection').hide();
	$('#edit-screen-form').hide();
	currentPage=1;
	var screen=JSON.parse($(this).attr("screen"));
	screen_id=screen.screen_id;
	screen_name=$(this).text();
	localStorage.setItem('screen_id', screen_id);
	localStorage.setItem('screen_name', screen_name);
	//console.log("Theater id : "+theater_id);
	$.ajax({
		  url: './webapi/theater/'+theater_id+'/screen/'+screen_id+"/seatingArrangement",
	      method: 'GET',
	      success: function(response) 
	      {
		    console.log(response.seats.length);
	        if(response.seats.length!=0)
	        {
				displayResult(response);
			}
			else
			{
				getSeatingForm();
			}
	      }
	    });
});
  
  
  function getSeatingForm()
  {
	$('#screens').hide();
	$('#result-seating-container').hide();
	$('#form-container h1').text(theater_name+" "+screen_name+" Seating Layout");
	$('#form-container').show();
	$('#result-seating-container h1').text(theater_name+" "+screen_name+" Seating Layout");
	
  }
	
  $('#theater-form').submit(function(e) {
    e.preventDefault();
    
    var rows = parseInt($('#rows').val());
    var columns = parseInt($('#columns').val());
    
    total_columns=columns;
    total_rows=rows;
    remaining_rows=rows;
    
    //$('#seat-type-form input[type="number"]').val('');
    $('#premium-rows').attr('max', remaining_rows);
    $('#vip-rows').attr('max', remaining_rows);
    
    
    $('#vip-rows').val(0);
    $('#premium-rows').val(0);
    $('#normal-rows').val(remaining_rows);
    
    
    
    
    generateSeatingLayout(rows, columns);
    //$('#form-container').hide();
    $('#seating-container').show();
  });
  
  

  $('#confirm-button').click(function(event) {
	//alert("Inside confirm");
	//console.log("preventing");
	event.preventDefault();
	
    var selectedSeats = $('.seat.selected').map(function() {
      return $(this).attr('data-seat');
    }).get();//get changes jquery array returned by .map() to normal js array
    
    //alert(selectedSeats);
    
    var vip = parseInt($('#vip-rows').val());
    if(isNaN(vip))
    {
		vip=0;
    }
    var premium = parseInt($('#premium-rows').val());
    if(isNaN(premium))
    {
		premium=0;
    }
    var normal = parseInt($('#normal-rows').val());
    if(isNaN(normal))
    {
		normal=0;
    }
    var total = vip+ premium + normal;
    console.log(vip+" "+premium+" "+normal);
    
    if (total != total_rows) {
      alert('Row count calculation is mismatch with total rows..Check again');
      return 0;
    }
    console.log(selectedSeats);
    saveSelectedPath(selectedSeats,theater_id,screen_id,total_rows,total_columns,vip,premium,normal);
  });
  
  
   $('#vip-rows').on('input', function() {
     var vip = parseInt($(this).val());
     var max=parseInt($('#vip-rows').attr("max"));
     if(vip > max)
     {
		alert("VIP row count should be less than or equal to "+max);
		$(this).val(0);
		//$(this).focus();
		return 0;
     }
     remaining_rows = total_rows - vip;
     $('#premium-rows').attr('max',remaining_rows);
     $('#premium-rows').val(0);
     $('#normal-rows').val(remaining_rows);
  });
  
  
  $('#premium-rows').on('input', function() {
	var vip = parseInt($('#vip-rows').val());
	console.log("vip value "+vip);
	if(isNaN(vip))
	{
		alert("Enter VIP row count..");
		$(this).val(0);
		//$('#vip-rows').focus();
		return 0;
	}
    var premium = parseInt($(this).val());
    var max=parseInt($('#premium-rows').attr("max"));
    if(premium > max)
    {
		alert("Premium row count should be less than or equal to "+max);
		$(this).val(0);
		//$(this).focus();
		return 0;
    }
    
    remaining_rows = total_rows - vip - premium;
    
    /*
    var selectedSeats = $('.seat.selected').map(function() {
      return $(this).attr('data-seat');
    }).get();
   */
    $('#normal-rows').val(remaining_rows);
    
    
  });
  
  
  function saveSelectedPath(selectedSeats,theater_id,screen_id,total_rows,total_columns,vip,premium,normal) 
  {
	
	var data={
		total_rows:total_rows,
		total_columns:total_columns,
		vip:vip,
		premium:premium,
		normal:normal,
		path:selectedSeats
	};
	
    $.ajax({
      url: './webapi/theater/'+theater_id+'/screen/'+screen_id+"/seatingArrangement",
      method: 'POST',
      data: JSON.stringify(data),
      contentType: "application/json",
      success: function(response) {
        displayResult(response);
      }
    });
  }

  function generateSeatingLayout(rows, columns) {
    var seatingLayout = $('#seating-layout');
    seatingLayout.empty();
    seatingLayout.append('<div class="char"></div>');
    for(var i=1;i<=columns;i++)
    {
		seatingLayout.append('<div class="char">'+i+'</div>');
	}
	seatingLayout.append('<br>');
    for (var i = 1; i <= rows; i++) {
		var char=String.fromCharCode(i + 64);
		seatingLayout.append('<div class="char">'+char+'</div>');
      for (var j = 1; j <= columns; j++) {
        var seatNumber = char + '' + j;
        seatingLayout.append('<div class="seat" id="' + seatNumber + '" data-seat="' + seatNumber + '">'+"&nbsp"+'</div>');
      }
      seatingLayout.append('<br>');
    }
  }

	  /*.seat is dynamically created so you can't you normal click(used for element which is already presented in DOM at initial).have to use parent.on(event,dynamic decendent,action)
	  $(document).on('click', '.seat', function() {
	    $(this).toggleClass('selected');
	  });
	  */
	  
  
    var isMouseDown = false;
    
    $(document).on('mousedown','.seat',function(){//pressing rightclick
      isMouseDown = true;
      $(this).toggleClass('selected');
       return false; // to prevent text selection while dragging which is browser is default behaviour of text selection
    });
   
    
    $(document).on('mouseover','.seat',function(){
      if (isMouseDown) {
        $(this).toggleClass('selected');
      }
    });

    $(document).on('mouseup',function(){//releasing rightclick
      isMouseDown = false;
    });
     

  function displayResult(response) 
  {
	    console.log(response);
	    var seats=response.seats;
	    var columns=response.columns;
	    var rows=parseInt(seats.length/columns);
    	$('#form-container').hide();
    	$('#seating-container').hide();
    	$('#screens').hide();
    	$('#result-seating-container h1').text(theater_name+" "+screen_name+" Seating Layout");
    	$('#result-seating-container').show();
    	
    	
    	var seatingLayout = $('#result-seating-layout');
	    seatingLayout.empty();
	    
	    seatingLayout.append('<div class="char"></div>');
	    for(var i=1;i<=columns;i++)
	    {
			seatingLayout.append('<div class="char">'+i+'</div>');
		}
		seatingLayout.append('<br>');
		var k=0;
	    for (var i = 1; i <= rows; i++) {
			var char=String.fromCharCode(i + 64);
			seatingLayout.append('<div class="char">'+char+'</div>');
	      for (var j = 1; j <= columns; j++) {
	        var seat=seats[k];
	        
	        var seatDiv=$('<div>')
	        .addClass('seat')
	        .append("&nbsp;");
	        
	        if(seat.seat_type=='P')
	        {
				seatDiv.addClass('path');
	        }
	        else
	        {
				console.log(seat.seat_type.toLowerCase());
				seatDiv.addClass(seat.seat_type.toLowerCase());	
			}
	        
	        seatingLayout.append(seatDiv);
	        k++;
	      }
	      seatingLayout.append('<br>');
    }
  }
  
  $('#reset-seatings-button').on('click',function(event){
	event.preventDefault();
	console.log("preventing");
	if (confirm("Are you sure you want to reset the seating arrangement?All the shows created with this seating will be cancelled..")) {
		getSeatingForm();
		}
 });
  
   $('#edit-seatings-button').on('click',function(event){
	event.preventDefault();
	console.log("preventing");
	if (confirm("Are you sure you want to edit the seating arrangement?All the shows created with this seating will be cancelled..")) 
	{
		screen_id=localStorage.getItem('screen_id');
	    screen_name=localStorage.getItem('screen_name');
	    theater_name=localStorage.getItem('theater_name');
	    $('#form-container').show();
    	$('#seating-container').show();
    	$('#form-container h1').text(theater_name+" "+screen_name+" Seating Layout");
    	$('#result-seating-container').hide();
	//console.log("Theater id : "+theater_id);
	     $.ajax({
		  url: './webapi/theater/'+theater_id+'/screen/'+screen_id+"/seatingArrangement",
	      method: 'GET',
	      success: function(response) 
	      {
		     var seats=response.seats;
	         var columns=response.columns;
	         var rows=parseInt(seats.length/columns);
	         
	         var vip=0,premium=0,normal=0;
	        
	        $('#rows').val(rows);
	        $('#columns').val(columns);
		    var seatingLayout = $('#seating-layout');
	    	seatingLayout.empty();
	    
	    	seatingLayout.append('<div class="char"></div>');
	    	for(var i=1;i<=columns;i++)
	    	{
				seatingLayout.append('<div class="char">'+i+'</div>');
			}
			seatingLayout.append('<br>');
			var k=0;
	    	for (var i = 1; i <= rows; i++) {
			var char=String.fromCharCode(i + 64);
			seatingLayout.append('<div class="char">'+char+'</div>');
	      	for (var j = 1; j <= columns; j++) {
	        var seat=seats[k];
	        
	        var seatDiv=$('<div>')
	        .addClass('seat')
	        .attr("data-seat",char+''+j)
	        .append("&nbsp;");
	        
	        
	        if(seat.seat_type=='P')
	        {
				seatDiv.addClass('selected');
	        }
	        if(seat.seat_type==='VIP')
	        vip++;
	        if(seat.seat_type==='Premium')
	        premium++;
	        if(seat.seat_type==='Normal')
	        normal++;
	        
	      
	        seatingLayout.append(seatDiv);
	        k++;
	      	}
	      	seatingLayout.append('<br>');
	      
    		}
    		
    		vip=parseInt(vip/columns);
    		premium=parseInt(premium/columns);
    		normal=parseInt(normal/columns);
    		
    		total_columns=columns;
		    total_rows=rows;
		    remaining_rows=total_columns;
		    
		    //$('#seat-type-form input[type="number"]').val('');
		    $('#premium-rows').attr('max', total_rows);
		    $('#vip-rows').attr('max', total_rows);
		    
		    
		    $('#vip-rows').val(0);
		    $('#premium-rows').val(0);
		    $('#normal-rows').val(rows);

	      }
	    });
	}
 });
  
 
 
    var movieList;
	var movie_name;
	var movie_id,selected_movie;
	var start_date,end_date,start_time,end_time;
	var show_duration;
	var theater_id=parseInt(localStorage.getItem("theater_id"));
	var screen_id=parseInt(localStorage.getItem("screen_id"));
	
    var now=new Date();
	var today = now.toISOString().split('T')[0];
	$('#start-date').attr('min',today);
	$('#end-date').attr('min',today);
	
	
	$(document).on('click','#show-details-button',function(event){
	event.preventDefault();
	//alert('Hii '+localStorage.getItem("theater_name"));
	$('#result-seating-container').hide();
	$('#add-show-div h1').text(localStorage.getItem("theater_name")+" "+localStorage.getItem("screen_name"));
	$('#add-show-div').show();
	$('#shows-div').show();
	refreshShowList();
	
	
		$.ajax({
	      url: './webapi/movie',
	      type: 'GET',
	      success: function(data) {
	  		movieList=data;
	  		
	        data.forEach(function(item) {
	          var optionItem = $('<option>')
	                          .attr("value",item.name);
	          $('#movies').append(optionItem);
	        });
            },
            error :function(error)
            {
				console.log("Error in getting movielist");
				alert("Error in getting movielist");
			}
        });
        
        
 	});
 	
 	$('#movie-name').on('change',function()
 	{
	   $('#language').empty();
	   movie_name=$('#movie-name').val();
	   var filteredArray = $.grep(movieList, function(obj) {
 	   return obj["name"] === movie_name;
	   });
	  
	   var result = filteredArray[0].languages;
	   selected_movie=filteredArray[0];
	   result.forEach(function(item) {
	          var optionItem = $('<option>')
	            .attr("value",item)
	            .text(item);
	          $('#language').append(optionItem);
	   });
	   
	   $("#add-show-form2").hide();
	   $("show-available-slots-row").show();
	   $('#movie-duration').val(selected_movie.duration);
	   $('#show-duration').attr('min',selected_movie.duration);
	    $('#show-duration').val('');
	    $('#slots-td').empty();
	   var hours=parseInt(selected_movie.duration.split(":")[0]);
	   var mins=parseInt(selected_movie.duration.split(":")[1]);
	   
	   var diffMinutes = ((23*60)+59) - ((hours*60)+mins);
  	   var diffHours = Math.floor(diffMinutes / 60);
  	   var diffMinutesRemaining = diffMinutes % 60;
	   var diffTime = ("0" + diffHours).slice(-2) + ":" + ("0" + diffMinutesRemaining).slice(-2);
	   
       //$('#start-time').attr('max',diffTime);
       
      
      
	  });
	
	$('#start-date').on('change',function()
 	{
		now=new Date();
		start_date=$('#start-date').val();
		$('#end-date').attr('min',start_date);
		//alert(start_date);//2023-05-20
		// If start date is today, use current time as start time
		if (new Date(start_date).toDateString() === now.toDateString()) //Wed Mar 13 2002
		{
      	var currentHours =now.getHours();
      	var currentMinutes = now.getMinutes();
        var minStartTime = ("0"+currentHours).slice(-2) + ':' + ("0"+currentMinutes).slice(-2);
       $('#start-time').attr('min',minStartTime);
    	}
    	else
    	{
			$('#start-time').attr('min','00:00');
        }
        
         $("#add-show-form2").hide();
		 $("#show-available-slots-row").show();
		
	})
	$('#start-time').on('change',function()
 	{
		  $("#add-show-form2").hide();
		 $("#show-available-slots-row").show();
	});
	$('#end-time').on('change',function()
 	{
		  $("#add-show-form2").hide();
		 $("#show-available-slots-row").show();
	});
	$('#show-duration').on('change',function()
 	{
		  $("#add-show-form2").hide();
		 $("#show-available-slots-row").show();
	});
	$('#start-time').on('blur',function()
 	{
		
		var start_time=$('#start-time').val();
		var min_start_time=$('#start-time').attr('min');
		var hours = parseInt(start_time.split(":")[0]);
        var minutes = parseInt(start_time.split(":")[1]);
        
        if(start_time < min_start_time)
        {
			 alert("Show start time is invalid..Kindly check");
			 $(this).val('');
			 $('#slots-td').empty();
        }
        
		
	});
	
	var alertShown = false;
	$('#show-duration').on('blur',function()
 	{
		show_duration=$('#show-duration').val();
		min_show_duration=$('#show-duration').attr('min');
		var hours=show_duration.split(':')[0];
		if(parseInt(hours)>=5 || show_duration < min_show_duration)
		{
			alert('Show duration should be greater than '+ min_show_duration+' less than 5 hours');
			 $(this).val('');
			 $('#slots-td').empty();
      		//alertShown = true;
    	} 
    	//else { alertShown = false;}
	});
	
	$('#start-date','#show-duration','#start-time').on('change',function()
	{
		 $("#add-show-form2").hide();
		 $("#show-available-slots-row").show();
		 
	});
	
	$("#add-show-form").on('submit', function(e) 
	{
	  e.preventDefault();
	  if(start_time!=='' && end_time!=='' && end_date!=='' && show_duration!=='')
      {
	
		var hours=show_duration.split(':')[0];
		if(parseInt(hours)>=5 && !alertShown)
		{
			alert("show duration should be less than 5 hours");
			 $(this).focus();
      		alertShown = true;
    	} else 
    	{
      			alertShown = false;
      		
    	}
    	
    	
	  $("#add-show-form2").show();
	  $("#show-available-slots-row").hide();
	  $('#slots-td').empty();
	  //if(!($('#start-date').is(':focus') && $('#end-date').is(':focus') && $('#start-time').is(':focus') && $('#end-time').is(':focus')))
	  //return;
      start_time=$('#start-time').val();
      start_date=$('#start-date').val();
      end_date=$('#end-date').val();
      show_duration=$('#show-duration').val();
      //max_start_time=$('#start-time').attr('max');
      
      
		screen_id=parseInt(localStorage.getItem("screen_id"));
		var data={
			'start_time':start_time,
			'start_date':start_date,
			'end_date':end_date,
			//'max_start_time':max_start_time,
			'show_duration':show_duration
		     };
		     
		     var td=$('#slots-td');
		      $.ajax({
		      url: './webapi/screen/'+screen_id+'/availableSlots',
		      type: 'POST',
		      data: JSON.stringify(data),
		      contentType:'application/json',
		      success: function(response) 
		      {
			    console.log(response);
				$.each(response,function(index,slot)
				{
					var checkbox = $('<input>').attr({
							        type: 'checkbox',
							        name: 'slot',
							        value: slot.start_time + '-' + slot.end_time
							      });
      
      				var label = $('<label>').text(slot.start_time + '-' + slot.end_time);
      				td.append(checkbox,label,$('<br>'));
				});
		      }
		    });
      }
      
  });
    
	$('#add-show-form2').submit(function(e) {
    e.preventDefault();
    
    var isChecked = $('input[name="slot"]:checked').length > 0;

	  if (!isChecked) {
	   alert('Please select at least one slot.');
	   return;
	  }
        
        addShow();
   
   });
   
   function addShow()
   {
	//serialize Formparams----movie-name=Mankatha&language=Tamil&start-date=2023-05-21&end-date=2023-05-22&
	var formDataAsJsonArray = $('#add-show-form').serializeArray();
	var formDataAsJsonArray2 = $('#add-show-form2').serializeArray();
	console.log(formDataAsJsonArray);
    var jsonObject={};
    $(formDataAsJsonArray).each(function(index,field) {
	  
	  if(field.name==='slot')
	  {
		 if(!jsonObject[field.name]) 
		 {
         	jsonObject[field.name]=[];
         }
         jsonObject[field.name].push(field.value);
      }
	  else
      {
          jsonObject[field.name] = field.value;
      }
    });
    $(formDataAsJsonArray2).each(function(index,field) {
	  
	  if(field.name==='slot')
	  {
		 if(!jsonObject[field.name]) 
		 {
         	jsonObject[field.name]=[];
         }
         jsonObject[field.name].push(field.value);
      }
	  else
      {
          jsonObject[field.name] = field.value;
      }
    });
    
    jsonObject["movie_id"]=selected_movie.movie_id;
    //alert("selected screen_id "+screen_id);  
    theater_id=parseInt(localStorage.getItem("theater_id"));
	screen_id=parseInt(localStorage.getItem("screen_id"));
    
    $.ajax({
      url: './webapi/theater/'+theater_id+'/screen/'+screen_id+'/show',
      type: 'POST',
      data: JSON.stringify(jsonObject),
      contentType: 'application/json',
      success: function(data) {
	    if(data.length===0)
	    {
		    alert("Shows added successfully..");
		    $('#add-show-form').trigger('reset');
	    	$('#language').empty();
	    	$('#slots-td').empty();
	    	 $("#add-show-form2").hide();
		     $("#show-available-slots-row").show();
	        refreshShowList();
	    }
	    else
	    {
		    var msg ="Below slots are not available for new show..Kindly check\n";
		    $.each(data,function(index,item){
			msg=msg+item+"\n";
		    });
		    msg=msg+"All the other shows are added successfully!!";
		    console.log(msg);
			alert(msg);
			refreshShowList();
			
		}
      }
    });
   } 
   
   
   function refreshShowList()
   {
		theater_id=parseInt(localStorage.getItem("theater_id"));
		screen_id=parseInt(localStorage.getItem("screen_id"));
		$.ajax({
			url: './webapi/theater/'+theater_id+'/screen/'+screen_id+'/show',
		      type: 'GET',
		      success: function(data) {
			    if(data.length>0)
			    {
				    shows=data;
		            getPaginatedShowsView();
		        }
		        else
			    {
					var row=$("<tr>");
					row.append($("<td colspan='14'>").text("No shows added."));
					$("#shows-table").append(row);
				}
		    }
		});
	}
	
	function getPaginatedShowsView()
	{
		let pagination=$("#shows-table-pagination");
		let totalRows=shows.length;
		pagination.empty();
        var totalPages = Math.ceil(totalRows / rowsPerPage);
        
        for (var i = 1; i <= totalPages; i++) {
            var pageLink = $('<a>')
            .attr("href","javascript:void(0)")
            .attr("pageNumber",i)
			.text(" "+i);
            pageLink.click(function (event) {
				event.preventDefault();
                currentPage = parseInt($(this).attr("pageNumber"));
                fetchCurrentPaginatedShows();
            });
            pagination.append(pageLink);
        }
        
        
        if(currentPage>totalPages)
        currentPage=totalPages;
        
        fetchCurrentPaginatedShows();
     }
        
     function fetchCurrentPaginatedShows()
     {   
		var startIndex = (currentPage - 1) * rowsPerPage;
        var endIndex = startIndex + rowsPerPage;
		var table = $("#shows-table");
		table.find("tr:not(:first)").remove();
        for (var i = startIndex; i < endIndex && i < shows.length; i++) {
			let object=shows[i];
			var row = $("<tr>");
            row.append($("<td>").text(i+1));
            row.append($("<td>").text(object.show_date));
            row.append($("<td>").text(object.start_time));
            row.append($("<td>").text(object.movie_name));
            row.append($("<td>").text(object.language));
            row.append($("<td>").text(object.end_time));
            row.append($("<td>").text(object.vip_prize));
            row.append($("<td>").text(object.premium_prize));
            row.append($("<td>").text(object.normal_prize));
            row.append($("<td>").text(object.vip_cancel));
            row.append($("<td>").text(object.premium_cancel));
            row.append($("<td>").text(object.normal_cancel));
            //row.append($("<td>").text(object.end_time));
            
            
            var t=new Date(object.show_date+'T'+object.start_time);
            var now=new Date();
          
            if(object.status.toLowerCase()=='cancelled')
            {
				row.append($("<td colspan='3'>").text("Show cancelled"));
            }
            else
            {
				if(t<now)
				{
					 row.append($("<td>").text('-'));
				}
				else{
	                 var l=$("<button>")
	                .attr("show",JSON.stringify(object))
	                .attr("href","javascript:void(0)")
	                .attr("class","showEditLink")
	                .text("Edit Prize");
	                 var td=$("<td>").append(l);
	                 row.append(td);
                 }
	                 l=$("<button>")
	                .attr("show",JSON.stringify(object))
	                .attr("href","javascript:void(0)")
	                .attr("class","showCollectionLink")
	                .text("Collection");
	                 td=$("<td>").append(l);
	                 row.append(td);
            	if(t<now)
				{
					 row.append($("<td>").text('-'));
				}
				else
				{
	                 l=$("<button>")
	                .attr("show",JSON.stringify(object))
	                .attr("href","javascript:void(0)")
	                .attr("class","showDeleteLink")
	                .text("Delete");
	                 td=$("<td>").append(l);
	                 row.append(td);
	              }
            }
            
            table.append(row);
		}
	}
   
   /*
   function refreshShowList()
   {
		theater_id=parseInt(localStorage.getItem("theater_id"));
		screen_id=parseInt(localStorage.getItem("screen_id"));
		$.ajax({
			url: './webapi/theater/'+theater_id+'/screen/'+screen_id+'/show',
		      type: 'GET',
		      success: function(data) {
			    if(data.length>0)
			    {
				    var table = $("#shows-table");
		            table.find("tr:not(:first)").remove();
		            var i=1;
		            $.each(data, function(index, object) {
		                var row = $("<tr>");
		                row.append($("<td>").text(i));
		                row.append($("<td>").text(object.show_date));
		                row.append($("<td>").text(object.start_time));
		                row.append($("<td>").text(object.movie_name));
		                row.append($("<td>").text(object.language));
		                row.append($("<td>").text(object.end_time));
		                row.append($("<td>").text(object.vip_prize));
		                row.append($("<td>").text(object.premium_prize));
		                row.append($("<td>").text(object.normal_prize));
		                row.append($("<td>").text(object.vip_cancel));
		                row.append($("<td>").text(object.premium_cancel));
		                row.append($("<td>").text(object.normal_cancel));
		                //row.append($("<td>").text(object.end_time));
		                
		                
		                var t=new Date(object.show_date+'T'+object.start_time);
		                var now=new Date();
		              
		                if(object.status.toLowerCase()=='cancelled')
		                {
							row.append($("<td colspan='3'>").text("Show cancelled"));
		                }
		                else
		                {
							if(t<now)
							{
								 row.append($("<td>").text('-'));
							}
							else{
				                 var l=$("<button>")
				                .attr("show",JSON.stringify(object))
				                .attr("href","javascript:void(0)")
				                .attr("class","showEditLink")
				                .text("Edit Prize");
				                 var td=$("<td>").append(l);
				                 row.append(td);
			                 }
				                 l=$("<button>")
				                .attr("show",JSON.stringify(object))
				                .attr("href","javascript:void(0)")
				                .attr("class","showCollectionLink")
				                .text("Collection");
				                 td=$("<td>").append(l);
				                 row.append(td);
		                	if(t<now)
							{
								 row.append($("<td>").text('-'));
							}
							else
							{
				                 l=$("<button>")
				                .attr("show",JSON.stringify(object))
				                .attr("href","javascript:void(0)")
				                .attr("class","showDeleteLink")
				                .text("Delete");
				                 td=$("<td>").append(l);
				                 row.append(td);
				              }
		                }
		                
		                table.append(row);
		                i++;
		            });
			       
			    }
			    else
			    {
					var row=$("<tr>");
					row.append($("<td colspan='14'>").text("No shows added."));
					$("#shows-table").append(row);
				}
		      }
		});
   }
   */
   $(document).on('click','.showCollectionLink',function(e){
		e.preventDefault();
		$('#add-show-div').hide();
		$('#edit-show-div').hide();
		$('#show-collection-div').show()
		window.scrollTo(0, 0);
		
		var object=JSON.parse($(this).attr("show"));
		var id=object.show_id;
		//$('#show-collection-div h2').html(object.movie_name);
		
		$.ajax({
	    url: './webapi/show/' + id+'/collection',
	    type: 'GET',
	    success: function(data) 
	    {
	    	//alert(data);
        	viewCollection(data);
      	}
    });
		
    });
    
    
    function viewCollection(data)
    {
		var table=$('#show-collection-table');
		table.find("tr:not(:first)").remove();
		            var collection=0;;
		            $.each(data, function(index, object) {
		                var row = $("<tr>");
		                row.append($("<td>").text(object.user));
		                row.append($("<td>").text(object.count));
		                row.append($("<td>").text(object.seats));
		                row.append($("<td>").text(object.status));
		                row.append($("<td>").text(object.prize));
		                row.append($("<td>").text(object.refund));
		                var t=parseInt(object.prize)-parseInt(object.refund);
		                row.append($("<td>").text(t));
		                collection=collection+t;
		                table.append(row);
		                });
		                
		                var row=$("<tr>");
		                var closeButton = $('<button>')
            			.text('close')
            			.attr('id','showCollectionCloseButton');
            		    row.append($('<td>').append(closeButton));
            		    
		               
		               row.append($("<td colspan='5'>").text("Total Collection"));
		               row.append($("<td>").text(collection));
		               
		               table.append(row);               
    }
    
    $(document).on('click','#showCollectionCloseButton',function()
    {
		$('#show-collection-div').hide()
    });
    
    $(document).on('click','.showDeleteLink',function(e){
	e.preventDefault();
	if (confirm("Are you sure you want to delete the show?All the bookings for that show will be cancelled..")) 
	{
		var id=JSON.parse($(this).attr("show")).show_id;
		$.ajax({
	    url: './webapi/show/' + id+'/cancel',
	    type: 'PUT',
	    success: function(data) 
	    {
	    alert(data);
        refreshShowList();
        $('#show-collection-div').hide();
      }
    });
		}
    });
    
    
    $(document).on('click','.showEditLink',function(e){
	    e.preventDefault();
	    $('#add-show-div').hide();
	    $('#show-collection-div').hide()
	    $('#edit-show-div').show();
	    
	    var show=JSON.parse($(this).attr("show")); 
		var id=show.show_id;
		
		localStorage.setItem('show_id',id);
		
		$('#edit-movie-name').val(show.movie_name);
		 var optionItem = $('<option>')
	            .attr("value",show.language)
	            .text(show.language);
	    $('#edit-language').append(optionItem);
		$('#edit-language').val(show.language);
		$('#edit-show-date').val(show.show_date);
		$('#slot-td').text(show.start_time+' - '+show.end_time);
		$('#edit-vip-prize').val(show.vip_prize);
		$('#edit-premium-prize').val(show.premium_prize);
		$('#edit-normal-prize').val(show.normal_prize);
		$('#edit-vip-cancel').val(show.vip_cancel);
		$('#edit-premium-cancel').val(show.premium_cancel);
		$('#edit-normal-cancel').val(show.normal_cancel);
		window.scrollTo(0, 0);
		
    });
    
   
   $('#edit-show-form').submit(function(e) {
	//alert("edit submit");
    e.preventDefault();
    
    var id=parseInt(localStorage.getItem("show_id"));
    var data={
		vip_prize :$('#edit-vip-prize').val(),
		premium_prize :$('#edit-premium-prize').val(),
		normal_prize :$('#edit-normal-prize').val(),
		vip_cancel :$('#edit-vip-cancel').val(),
		premium_cancel :$('#edit-premium-cancel').val(),
		normal_cancel :$('#edit-normal-cancel').val()
    };
    
    $.ajax({
	    url: './webapi/show/' + id,
	    type: 'PUT',
	    data:JSON.stringify(data),
	    success: function(response) 
	    {
	      alert("Updation successful..");
	      
	      $('#add-show-div').show();
	      $('#edit-show-div').hide();
          refreshShowList();
           $('#show-collection-div').hide();
        }
       });
    
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
