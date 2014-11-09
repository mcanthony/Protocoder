/*
*	Reference
*/ 

var Reference = function() { 

}

//parse the help comming directly from the android and create a structure for it
Reference.prototype.parseHelp = function (docString) {
	//console.log(docString);
    var that = this;

	var ds = docString;
	this.doc = JSON.parse(docString).apiClasses;

	this.index = lunr(function () {
		this.field('class', {boost: 10});
	    this.field('method', {boost: 10});
	    this.field('description');
	    this.field('example');
	    this.ref('id');
	})

	var countReturns = 0;

	var container = "#sidebar_container"

	$(container).load("reference2.html", function() {

	//iterate through classes 
	$.each(that.doc, function(k, v) {
	    //all
	    //console.log(v);

	    //class 
	    var className = v.name.substr(1, v.name.length).toLowerCase();

	    //append to main area or secondary objects 
	    //console.log("appending " + className + " " + v.isMainObject);

	    var where;
	    if (v.isMainObject) {
	    	where = "#primary";
	    } else {
	    	where = "#secondary";
	    }

    	$('<a class = "obj" target="_self">'+ className +'</a>')
    		.appendTo(container + " #ref_container " + where)
    		.click(function(){
    			$("#class_" + className).show();
    			$("#api_class_backdrop").show();
    		});


	    $(container + " #ref_container #api_class_details").append('<div id = "class_'+className+'" class = "api_class"><div id = "title"><h2>'+ className +'</h2></div> <div class = "methods"> </div></div>');
	   
	    $('<i id = "close" class = "fa fa-times"></i>').appendTo("#class_" + className + " #title").click(function() {
	    	$("#class_" + className).hide();
	    	$("#api_class_backdrop").hide();
	    });

	    //iterate through api methods 
	    $.each (v.apiMethods, function(m, n) {  
	    	//console.log("methods --> ");
	    	//console.log(n);

	        var method = n;

	        that.index.add(method);

	        //className 
	        var m = $('<div id ="method_'+ method.name +'" title = "' + method.description + '" class = "APImethod"></div>');
	        $(container + ' #ref_container #class_'+className + ' .methods').append(m);
	      
			//method [return] methodName [parameters]      
			var parameters = "";
			if (typeof method.parametersName !== "undefined") {
				parameters = method.parametersName.join(", ");
			}

			//if return type is void dont show it 
			if (method.returnType == "void") method.returnType = "";

	        $(container + " #ref_container #class_"+className + " #method_"+method.name).append('<h3><span id = "returnType">'+  method.returnType + " </span><strong>" + className + "." + method.name + '</strong><i>(<span id = "params">' +  parameters + '</span>)</i></h3>');
	        
	        //add description if exist 
	        if (method.description != undefined) { 
	        	$("#method_"+method.name).append('<p id = "description"> '+ method.description +' </p>');
	        }

	        //add example if exist 
	        if (method.example != undefined){ 
				$("#method_"+method.name).append('<p id = "example"> '+ "here goes example" /* method.example */ +' </p>');
	        	
	        	/* 
	        	$('<button> '+ method.name +' </button>')
	        		.click(function() {
	        			console.log("qq" + method.name)
	        		})
	        		.appendTo("#"+method.name);
	        	*/
	        	
	        }

 			//when click on reference insert text 
	        m.click(function() { 
	        	var returnType = "";
	        	if (method.returnType != "") {
	        		returnType = "var var" + countReturns++ + " = ";
	        	}

	        	// add curly braces when insert a callback
                var p = parameters.replace(")", "){ " + '\n' + '\n' + "}");

	        	protocoder.editor.editor.insert(returnType + "" + className + "." + method.name + "(" + p + ");"+'\n\n')
	        });
	       // console.log(method.name, method.description, method.example);
	    });
	});

	});
}

Reference.prototype.searchById = function (searchId) {
 for (var i = 0; i < this.doc.length; i++) {
  var methods = this.doc[i].apiMethods;
  for (var j = 0; j < methods.length; j++) {
    if (searchId == methods[j].id) {
     return methods[j];
    }
  }
 }
}