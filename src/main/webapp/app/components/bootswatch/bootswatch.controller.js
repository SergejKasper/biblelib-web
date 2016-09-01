(function() {
    'use strict';

    angular
        .module('bibelBibliothekApp')
        .controller('BootswatchController', BootswatchController);

    BootswatchController.$inject = ['$scope', 'BootSwatchService'];

    function BootswatchController ($scope, BootSwatchService) {
        var vm = this;

        getThemes();

        function getThemes () {
            /* Get the list of availabel bootswatch themes */
            BootSwatchService.get().then(function(themes) {
                var uglyThemes = ["Cosmo", "Cyborg", "Darkly", "Flatly", "Lumen", "Paper", "Readable", "Slate", "Superhero"];
                /*Check agains uglyness */
                vm.themes = themes.filter(function(theme){
                    return (uglyThemes.indexOf(theme.name) === -1);
                });
                vm.themes.unshift({name:'Default',css:''});
            });
        }
    }
})();
