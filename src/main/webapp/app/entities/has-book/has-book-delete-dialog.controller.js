(function() {
    'use strict';

    angular
        .module('bibelBibliothekApp')
        .controller('HasBookDeleteController',HasBookDeleteController);

    HasBookDeleteController.$inject = ['$uibModalInstance', 'entity', 'HasBook'];

    function HasBookDeleteController($uibModalInstance, entity, HasBook) {
        var vm = this;

        vm.hasBook = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            HasBook.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
