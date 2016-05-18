var configPage = configPage || {};

init = function() {
	$("#menu-config").addClass("active");
	configPage.loadTable();
	configPage.configureForm();
};

configPage.loadTable = function () {
	$.ajax({
		method: "GET",
		url: utils.getBaseUrl() +"proxies.json"
	})
	.done(function (proxies) {
		$("#proxiesTbody").empty();
		
		$.each( proxies, function( index, proxy ) {
			$("#proxiesTbody").append(configPage.createTableRow(proxy));
		});
		
		$("#proxiesTable").footable();
	});
	
	
};

configPage.createTableRow = function (proxy) {
	var row = $("<tr>")
	.append($("<td>").text(proxy.origin))
	.append($("<td>").text(proxy.destination))
	.append($("<td>")
				.append($("<a href='#'>")
							.data("proxyId", proxy.id)
							.click(configPage.deleteProxy)
							.append($('<span>')
										.addClass("glyphicon glyphicon-remove")
										.css("color", "red")
										.attr("aria-hidden", "true")
						))
				.append("&nbsp;&nbsp;")
				.append($("<a href='#'>")
							.data("proxy", proxy)
							.click(configPage.loadForm)
							.append($('<span>')
										.addClass("glyphicon glyphicon-pencil")
										.css("color", "green")
										.attr("aria-hidden", "true")
				))
			);
	return row;
}


configPage.deleteProxy = function (e) {
	e.preventDefault();
	var proxyId = $( this ).data("proxyId");
	bootbox.confirm("Do you want want to delete this proxy?", function(result) {
		if (result) {
			$.ajax({
				method: "DELETE",
				url: utils.getBaseUrl() +"proxies/" + proxyId
			})
			.done(function (proxies) {
				configPage.loadTable();
				configPage.successAlert("Proxy deleted");
			})
			.fail(function (e) {
				configPage.errorAlert("Error deleting: " + e);
			});			
		}
	}); 
};

configPage.loadForm = function (e) {
	e.preventDefault();
	
	var proxy = $( this ).data("proxy");
	$("#form-id").val(proxy.id);
	$("#form-origin").val(proxy.origin);
	$("#form-destination").val(proxy.destination);
};


configPage.configureForm = function () {
	$("#config-form").submit(function (e){
		e.preventDefault();
		
		var form = $( this );
		var btn = $("#submitRequest").button('loading');

		$.ajax({
			method: "POST",
			url: utils.getBaseUrl() +"proxies",
			data: form.serialize()
		})
		.done(function (proxies) {
			configPage.loadTable();
			form.trigger("reset")
		})
		.always(function () {
			btn.button("reset");
		});
	});
	
	$("#config-form").on("reset", function (e) {
		$("#config-form input:hidden").val('').trigger('change');
	});
	
};

configPage.clearAlerts = function () {
	$("#success-msg").hide();
	$("#error-msg").hide();
}

configPage.successAlert = function (msg) {
	configPage.clearAlerts();
	$("#success-msg").html(msg);
	utils.showTimedAlert("#success-msg");
}

configPage.errorAlert = function (msg) {
	configPage.clearAlerts();
	$("#error-msg").html(msg);
	utils.showTimedAlert("#error-msg");
}
