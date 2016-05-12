var utils = utils || {};

utils.getBaseUrl = function () {
	return $('head base').attr('href');
};


utils.configureNotification = function () {
	$.notifyDefaults({
		allow_dismiss: true,
		newest_on_top: false,
		placement: {
			from: "top",
			align: "right"
		},
		animate: {
			enter: 'animated fadeInDown',
			exit: 'animated fadeOutUp'
		},
		delay: 500,
		timer: 500,
	});	
};

utils.getIeVersion = function () {
    var ua = window.navigator.userAgent;

    var msie = ua.indexOf('MSIE ');
    if (msie > 0) {
        // IE 10 or older => return version number
        return parseInt(ua.substring(msie + 5, ua.indexOf('.', msie)), 10);
    }

    var trident = ua.indexOf('Trident/');
    if (trident > 0) {
        // IE 11 => return version number
        var rv = ua.indexOf('rv:');
        return parseInt(ua.substring(rv + 3, ua.indexOf('.', rv)), 10);
    }

    var edge = ua.indexOf('Edge/');
    if (edge > 0) {
       // IE 12 => return version number
       return parseInt(ua.substring(edge + 5, ua.indexOf('.', edge)), 10);
    }

    // other browser
    return 0;
};

utils.configureCsrf = function (token) {
	var header = "X-CSRF-TOKEN";
	$(document).ajaxSend(function(e, xhr, options) {
		xhr.setRequestHeader(header, token);
	});
};

utils.configureLogout = function() {
	$("#logout").click(function (e) {
		e.preventDefault();
		$("#formlogout").submit();
	});
}

utils.showTimedAlert = function (alertSelector) {
	$(alertSelector).fadeTo(2000, 500).slideUp(500, function(){
	    $(alertSelector).alert('close');
	});
};
