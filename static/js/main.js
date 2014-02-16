//web service on a different domain using jQuery's ajax method
$.support.cors = true;

//everything in this block will be executed on pageload
$(document).ready(function() {

	var sparqlEndpoints = [];

	//code block below to load endpoints on pageload
	{
		$("#sparqlTextArea").text(sampleSparqlQueries[0]);
		var editor = CodeMirror.fromTextArea(document.getElementById("sparqlTextArea"), {
	        mode: "application/x-sparql-query",
	        lineNumbers: true,
	        indentUnit: 4,
	        autofocus: true,
	        matchBrackets: true
	      });
		
		
		
		//console.log(sampleSparqlQueries[0]);
		//console.log("loadEndpoints");
		$.ajax({
			type: 'GET',
			headers: { 
				Accept : "application/json"
			},
			url: '/dqp/getEndpoints',
			//data: {'query':sparqlQuery},
			dataType: "json",
			crossDomain: true,
			success: function(data, textStatus, jqXHR){

				//console.log(textStatus);
				if(jqXHR.status == 200) {
					//console.log(data);
					addEndpointsTableHeader();
					var tbody = $("#tblEndpoint").find('tbody');

					$.each(data, function(index, item) {
						tbody.append($('<tr>').append($('<td>')
								.text(item)
						)
						.append($('<td>').append($('<button>').addClass('btn')
								.addClass('btn-xs')
								.addClass('btn-danger')
								.attr('type','button')
								.text('Remove')
								.attr('id','btnRemoveEndpoint')
						)

						)												
						);
						sparqlEndpoints.push(item);
					});
				}

			},
			error: function(jqXHR, textStatus, errorThrown){
				//console.log(jqXHR);
			}
		});
	}



	//handler for endpoint select change envent
	$('#endpointSelect').on('change', function (e) {
		var endpoint = $('#endpointSelect option:selected').html();
		if(endpoint == "Select") {
			$('#inputEndpointURL').val("");
		} else {
			$('#inputEndpointURL').val(endpoint);
		}
	});



	//handler for add endpoint button click envent
	$('#btnEndpoint').click( function (e) {

	    var btn = $(this);
	    btn.button('loading');		
		
		var endpoint = $('#inputEndpointURL').val();
		$('#inputEndpointURL').val(endpoint);



		$.ajax({

			type: 'POST',
			url: '/dqp/addEndpoint',
			data: {'endpoint':endpoint},
			//dataType: 'json',

			success: function(data, textStatus, jqXHR){
				//console.log(data);
				addEndpointsTableHeader();

				$("#tblEndpoint").find('tbody')
				.append($('<tr>')
						.append($('<td>')
								.text(endpoint)
						)
						.append($('<td>').append($('<button>').addClass('btn')
								.addClass('btn-xs')
								.addClass('btn-danger')
								.attr('type','button')
								.attr('data-loading-text','Removing...') 
								.text('Remove')
								.attr('id','btnRemoveEndpoint'))

						)
				);
				sparqlEndpoints.push(endpoint);

				//$("#addEndpointResponse").text("Endpoint successfully added");
				infoSuccess("Endpoint successfully added");


			},
			error: function(jqXHR, textStatus, errorThrown){
				console.log(jqXHR.status);
				console.log(jqXHR.responseText);
				if(jqXHR.status == 409) {
					//$("#addEndpointResponse").text("Endpoint already added");
					infoWarning("Endpoint already added");
				} else if(jqXHR.status == 404) {
					infoError("Endpoint not available");
				} else {
					infoError("Could not add endpoint");
				}

			}
		}).always(function () {
		      btn.button('reset');
	    });



	});

	//removes an endpoint
	$('#tblEndpoint').on("click", "#btnRemoveEndpoint", function(e) {
		var endpoint = $(this).closest("tr").children(":first").html(); 
		var btn = this;
		//console.log(endpoint);
	    var btn = $(this);
	    btn.button('loading');


		var res = false;
		$.ajax({

			type: 'POST',
			url: '/dqp/removeEndpoint',
			data: {'endpoint':endpoint},
			//dataType: 'json',

			success: function(data, textStatus, jqXHR){
				//console.log(data);
				//console.log(jqXHR.status);
				sparqlEndpoints.remove(endpoint);
				//console.log(sparqlEndpoints);
				$(btn).parent().parent().remove();
				//$("#addEndpointResponse").text("Endpoint successfully removed");
				infoSuccess("Endpoint successfully removed");
				
				//console.log(sparqlEndpoints);
				if(sparqlEndpoints.length==0) {
					//console.log('endpoints table header removed');
					$('#tblEndpoint thead tr').remove();
				}
			},
			error: function(jqXHR, textStatus, errorThrown){
				console.log(jqXHR.status);
				console.log(jqXHR.responseText);
				//$("#addEndpointResponse").text("Endpoint remove failed");
				infoError("Endpoint successfully removed");
			}
		}).always(function () {
		      btn.button('reset');
	    });

		console.log("ajax 2:"+res);
		return res;		
	});

	$('#btnQuery').click(function() {
	
	    var btn = $(this);
	    btn.button('loading');
	    
		var sparqlQuery = $('#sparqlTextArea').val();
		$.ajax({
			type: 'GET',
			headers: { 
				Accept : "application/sparql-results+json"
			},

			url: '/dqp/sparql',
			data: {'query':sparqlQuery},
			//dataType: "application/sparql-results+json",
			dataType: "json",
			crossDomain: true,
			success: function(data, textStatus, jqXHR){
				//console.log(data);
				renderResults(data);

			},
			error: function(jqXHR, textStatus, errorThrown){

				console.log(jqXHR.status);
				console.log(jqXHR.responseText);

				$('#tblRes thead tr').remove();
				$('#tblRes tbody tr').remove();

				infoError("Querying fialed: "+textStatus);

			}
		}).always(function () {
		      btn.button('reset');
	    });

	});	
	
	
	//explain a result
	$('#tblRes').on("click", "#btnExplainRes", function(e) {
	    var btn = $(this);
	    btn.button('Genering..');		
		var row = $(this).closest("tr");
		//var jsonObj = [];
		var item = {};
		//console.log(row);
		var i = 0;
		$.each(row[0].cells, function(){
			if(i==variableNames.length) return false;
	        //alert('hi');
			//console.log(variableNames[i]+"='"+$(this).html()+"'");
			
			item[variableNames[i]] = $(this).html();
			
			i = i+1;
	    });
		
		
		//console.log(item);
		var jsonString = JSON.stringify(item);
		console.log(jsonString);
		
		
		$.ajax({
			type: 'POST',
			url: '/dqp/explainResult',
			data: {'result': jsonString},
			//data:{'test':"test param"},
		    //contentType: "application/json; charset=utf-8",
		    //dataType: "json",	
			crossDomain: true,
			success: function(data, textStatus, jqXHR){
				console.log(jqXHR.status);
				console.log(data);
			},
			error: function(jqXHR, textStatus, errorThrown){

				console.log(jqXHR.status);
				console.log(jqXHR.responseText);


			}
		}).always(function () {
		      btn.button('reset');
	    });		
	});
});


//Useful functions for array handling
Array.prototype.contains = function(a) { return this.indexOf(a) != -1 };
Array.prototype.remove = function(a) {if (this.contains(a)){ return this.splice(this.indexOf(a),1)}};


var sampleSparqlQueries = ["PREFIX  : <http://example/>\n" +
                           "PREFIX  dc: <http://purl.org/dc/elements/1.1/>\n" +
                           "PREFIX  foaf: <http://xmlns.com/foaf/0.1/>\n" +
                           "SELECT  ?name ?friend_name\n"+
                           "WHERE\n"+
                           "{\n" +
                           "    ?book dc:title ?title .\n"+
                           "    ?book dc:creator ?author .\n"+
                           "    ?author foaf:name ?name .\n"+
                           "    ?author foaf:knows ?friend .\n" +
                           "    ?friend foaf:name ?friend_name .\n" +
                           "    FILTER(?title = \"Distributed Query Processing for Linked Data\")\n"+
                           "}\n"]

var variableNames = [];

//add header to the sparql endpoints table
function addEndpointsTableHeader () {
	var header = $("#tblEndpoint").find('th').text();
	//console.log("header ["+header+"]");
	if(!header.trim()) {
		var thead = $("#tblEndpoint").find('thead');
		var headRow = $('<tr>')
		
		headRow.append($('<th>')
						.text('SPARQL Endpoint(s)')

				);
		headRow.append($('<th>').text(''))
	
		//console.log("header empty");
		thead.append(headRow);
	}

}

function renderResults(data) {
	// JAX-RS serializes an empty list as null, and a 'collection of one' as an object (not an 'array of one')
	var listVal = data.results.bindings == null ? [] : (data.results.bindings instanceof Array ? data.results.bindings : [data.results.bindings]);
	var listVar = data.head.vars == null ? [] : (data.head.vars instanceof Array ? data.head.vars : [data.head.vars]);

	$('#tblRes thead tr').remove();
	$('#tblRes tbody tr').remove();

	//Rendering the headers
	variableNames = [];
	var thead = $("#tblRes").find('thead');
	var headRow = $('<tr>')
	$.each(listVar, function(index, item) {

		headRow.append($('<th>').text(item.trim()));
		variableNames.push(item.trim());
	});
	thead.append(headRow);

	//Rendering the values
	var tbody = $("#tblRes").find('tbody');
	$.each(listVal, function(index, item) {

		var row = $('<tr>');
		$.each(item, function(name, v) {

			row.append($('<td>').text(v.value.trim()));
		});
		row.append($('<td>').append($('<button>').addClass('btn')
				.addClass('btn-xs')
				.addClass('btn-success')
				.attr('type','button')
				.attr('data-loading-text','Generating...') 
				.text('Explain')
				//.attr('disabled','disabled')
				.attr('id','btnExplainRes')));	
		tbody.append(row);
	});
}

function infoWarning(message){
	var html = "<div class=\"alert alert-warning\"><button type=\"button\" class=\"close\" data-dismiss=\"alert\">&times;</button><strong>Warning!</strong> "+message+"</div>";
	$('#footer').prepend(html);
	alertTimeout(5000);
}
function infoSuccess(message){
	var html = "<div class=\"alert alert-success\"><button type=\"button\" class=\"close\" data-dismiss=\"alert\">&times;</button><strong>Success!</strong> "+message+"</div>";
	$('#footer').prepend(html);
	alertTimeout(5000);
}
function infoError(message){
	var html = "<div class=\"alert alert-danger\"><button type=\"button\" class=\"close\" data-dismiss=\"alert\">&times;</button><strong>Error!</strong> "+message+"</div>";
	$('#footer').prepend(html);
	alertTimeout(5000);
}

function alertTimeout(wait){
	setTimeout(function(){
		$('#footer').children('.alert:last-child').remove();
	}, wait);
}
