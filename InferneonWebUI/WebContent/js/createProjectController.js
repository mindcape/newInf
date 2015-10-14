$(function createProject() {
      $("#Save").click( function()
    		  {
  		alert('sss');
  	    var url = "http://localhost:8080/dmac-machine-learning-1.0-SNAPSHOT/api/v1/projectReckoner/createProject";
  	    var postData = $(this).serializeArray();
  	    alert(url);
  	    var formData = {
  	            'projectName'              : $('input[name=projectName]').val(),
  	            'userName'				   : $('input[name=DynamicTextBox)').val(),
  	            'projectSchema'			   : $('input[name=DynamicTextBox)').val()
  	    };
  	    
  	    
  	    $.ajax({
  	           type: "POST",
  	           url: url,
  	           data: formData,
  	           dataType    : 'json', 
               encode      : true,
  	           success: function(data)
  	           {
  	        	   alert(data);
  	           },
  	    		failure: function(data)
  	    		{
  	    			alert("Failed")
  	    		}
  	         })
  	  .done(function(data) {

          
          console.log(data); 

          
      });

  // stop the form from submitting the normal way and refreshing the page
  event.preventDefault();
/*  	    return true; 
*/  	});

  });
