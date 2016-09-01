(function() {
    'use strict';

    angular
        .module('bibelBibliothekApp')
        .directive('jhSwitchTheme', jhSwitchTheme);

    function jhSwitchTheme () {
        var vm = this;

        return {
            restrict: 'A',
            scope: {
                theme : '=jhSwitchTheme'
            },
            link: link
        };

        function link(scope, element, attrs) {
            var currentTheme = $("#bootswatch-css").attr('title');

            $("body").addClass('theme-' + currentTheme.toLowerCase());

            if(scope.theme.name === currentTheme){
                element.parent().addClass("active");
            }

            element.on('click',function(){
                $("#bootswatch-css").attr("href", scope.theme.css);
                $("body").removeClass(function (index, css) {
                    return (css.match (/(^|\s)theme-\S+/g) || []).join(' ');
                });
                $("body").addClass("theme-" + currentTheme.toLowerCase());
                $(".theme-link").removeClass("active");
                element.parent().addClass("active");
            });
        }
    }
})();
