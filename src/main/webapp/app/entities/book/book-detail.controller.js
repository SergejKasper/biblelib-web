(function() {
    'use strict';

    angular
        .module('bibelBibliothekApp')
        .controller('BookDetailController', BookDetailController);

    BookDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'DataUtils', 'entity', 'Book', 'Author', 'HasBook'];

    function BookDetailController($scope, $rootScope, $stateParams, previousState, DataUtils, entity, Book, Author, HasBook) {
        var vm = this;

        vm.book = entity;
        vm.previousState = previousState.name;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('bibelBibliothekApp:bookUpdate', function(event, result) {
            vm.book = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
