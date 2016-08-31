(function() {
    'use strict';

    angular
        .module('bibelBibliothekApp')
        .controller('BookDialogController', BookDialogController);

    BookDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'Book', 'HasBook', 'Author', '$http', 'AlertService', 'printHandler'];

    function BookDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, Book, HasBook, Author, $http, AlertService, printHandler) {
        var vm = this;

        vm.book = entity;
        vm.clear = clear;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.save = save;
        vm.hasbooks = HasBook.query();
        vm.authors = Author.query();

        vm.getBookInfoFromOpenlibrary = function(){
            return $http.jsonp('https://openlibrary.org/api/books?bibkeys=ISBN:' + vm.book.bookIsbn +'&jscmd=data&callback=JSON_CALLBACK').then(function(res){
                /*if(!res.result_count || !res.result_count < 1)*/
                if(!res.data['ISBN:' + vm.book.bookIsbn])return;
                var resp = res.data['ISBN:' + vm.book.bookIsbn];
                vm.addBook(resp.title,
                    resp.summary,
                    null,
                    resp.cover.large,
                    resp.authors.map(function(a){
                        return a.name;
                    }));
            }, function(err){
                alert("Ein Fehler ist aufgetreten: ", JSON.stringify(err));
                return;
            });
        };

        vm.getBookInfoFromGoogleBooks = function(){
            return $http.get('https://www.googleapis.com/books/v1/volumes?q=isbn:' + vm.book.bookIsbn).then(function(res){
                if(!res.data.items){
                    //http://isbndb.com/api/v2/json/W25MX2G5/books?q=9781470196158&cacheBuster=1472481718355
                    return false;
                }
                var resp = res.data.items[0].volumeInfo;
                var imageLink = null;
                if(resp.imageLinks) imageLink = resp.imageLinks.thumbnail;
                if (resp){
                    vm.addBook(resp.title,
                        resp.description,
                        resp.language,
                        imageLink,
                        resp.authors);
                }
                return true;
            }, function(err){
                alert("Ein Fehler ist aufgetreten: ", JSON.stringify(err));
                return false;
            });
        }

        vm.getBookInfo = function(){
            if(!vm.book.bookIsbn.length === 13 || !vm.book.bookIsbn.length === 10 ) return;
            vm.isSaving = true;
            vm.getBookInfoFromOpenlibrary().then(function(result){
                if(!result) vm.getBookInfoFromGoogleBooks();
            });
        };

        if($stateParams.isbn){
            vm.book.bookIsbn = $stateParams.isbn;
            vm.getBookInfo();
        }

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        vm.addBook = function addBook(title, description, language, cover, authors){
            vm.book.title = title;
            if(description){
                vm.book.description = description.substring(0, 252);
                if(vm.book.description.length === 252) vm.book.description = vm.book.description + "...";
            }
            switch(language){
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
            if(cover) vm.book.cover = cover;
            vm.book.coverContentType = "";
            var authorName = authors.join(", ");
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

        vm.generateBibleLibISBN = function generateBibleLibISBN(){
            vm.book.bookIsbn = Math.floor(Math.random() * 90000000) + 10000000;
        };

        vm.printLabel = function printLabel() {
            if (!vm.book.bookIsbn){
                alert("eigene ISBN darf nicht leer sein");
                return;
            }
            if(vm.book.bookIsbn.length > 8 ){
                alert("eigene ISBN darf nicht mehr als 8 Zeichen lang sein")
                return;
            }

            printHandler.print(vm.book.bookIsbn);
        }

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
            if(vm.copies > 0){
                vm.copies=--vm.copies;
                vm.book.id = null;
                save();
            }
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
