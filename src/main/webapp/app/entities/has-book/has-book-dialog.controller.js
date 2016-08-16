(function() {
    'use strict';

    angular
        .module('bibelBibliothekApp')
        .controller('HasBookDialogController', HasBookDialogController);

    HasBookDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'HasBook', 'Borrower', 'Book'];

    function HasBookDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, HasBook, Borrower, Book) {
        var vm = this;

        vm.hasBook = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.borrowers = Borrower.query();
        vm.books = Book.query({filter: 'no-borrowers'});

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.hasBook.id !== null) {
                HasBook.update(vm.hasBook, onSaveSuccess, onSaveError);
            } else {
                HasBook.save(vm.hasBook, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('bibelBibliothekApp:hasBookUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.borrowDate = false;
        vm.datePickerOpenStatus.returnDate = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
