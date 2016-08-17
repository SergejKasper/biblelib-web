(function() {
    'use strict';

    angular
        .module('bibelBibliothekApp')
        .controller('BookDialogController', BookDialogController);

    BookDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'Book', 'HasBook', 'Author', '$http'];

    function BookDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, Book, HasBook, Author, $http) {
        var vm = this;

        vm.book = entity;
        vm.clear = clear;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.save = save;
        vm.hasbooks = HasBook.query();
        vm.authors = Author.query();

        vm.getBookInfo = function(){
            if(!vm.book.bookIsbn.length === 13 || !vm.book.bookIsbn.length === 10 ) return;
            vm.isSaving = true;
            $http.get('https://www.googleapis.com/books/v1/volumes?q=isbn:' + vm.book.bookIsbn).then(function(res){
                var resp = res.data.items[0];
                if (resp){
                    vm.book.title = resp.volumeInfo.title;
                    vm.book.description = resp.volumeInfo.description.substring(0, 252);
                    if(vm.book.description.length === 252) vm.book.description = vm.book.description + "...";
                    switch(resp.volumeInfo.language){
                        case "en":
                        vm.book.language = "ENGLISCH";
                        break;
                        case "de":
                        vm.book.language = "DEUTSCH";
                        break;
                        case "ru":
                        vm.book.language = "RUSSISCH";
                        break;
                    }
                    vm.book.cover = resp.volumeInfo.imageLinks.thumbnail;
                    vm.book.coverContentType = "";
                    var authorName = resp.volumeInfo.authors.join(", ");
                    if(!vm.authors.some(function(author){
                        if(author.name === authorName){
                            vm.book.author = author;
                            vm.isSaving = false;
                            return true;
                        }
                        return false;
                    })){
                        Author.save({'name': authorName}, function(result){
                            vm.authors.push(result);
                            vm.book.author = vm.authors[vm.authors.length - 1];

                            vm.isSaving = false;
                        }, function(){
                            vm.isSaving = false;
                        });
                    }
                }

            }, function(err){
                alert(err);
            })
        };

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.book.id !== null) {
                Book.update(vm.book, onSaveSuccess, onSaveError);
            } else {
                Book.save(vm.book, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('bibelBibliothekApp:bookUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


        vm.setCover = function ($file, book) {
            if ($file && $file.$error === 'pattern') {
                return;
            }
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        book.coverContentType = $file.type;
                        book.cover = 'data:' + vm.book.coverContentType + ';base64,' + base64Data;
                    });
                });
            }
        };

    }
})();
