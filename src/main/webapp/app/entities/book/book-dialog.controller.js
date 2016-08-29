(function() {
    'use strict';

    angular
        .module('bibelBibliothekApp')
        .controller('BookDialogController', BookDialogController);

    BookDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'Book', 'HasBook', 'Author', '$http', 'AlertService'];

    function BookDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, Book, HasBook, Author, $http, AlertService) {
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
                if(!res.data.items){
                   /* $http.get('http://isbndb.com/api/v2/json/W25MX2G5/books?q=9781470196158&cacheBuster=1472481718355').then(function(res){
                        if(!res.result_count || !res.result_count < 1){
                            AlertService.error("Keine passenden Bücher gefunden.");
                            return;
                        }
                        var resp = res.data[0];
                        this.addBook(resp.title,
                            resp.summary,
                            resp.language,
                            resp.author_data.map(function(a){
                                // reformat: surename, name to name surname and return string[]
                                var name = a.name.split(", ");
                                return name[1] + " " + name[0];
                            }));
                    }, function(err){
                        alert("Ein Fehler ist aufgetreten: ", JSON.stringify(err));
                        return;
                    })*/
                }
                return;
                var resp = res.data.items[0].volumeInfo;
                if (resp){
                    this.addBook(resp.title,
                        resp.description,
                        resp.language,
                        resp.imageLinks.thumbnail,
                        resp.authors);
                }

            }, function(err){
                alert("Ein Fehler ist aufgetreten: ", JSON.stringify(err));
                return;
            })
        };

        if($stateParams.isbn){
            vm.book.bookIsbn = $stateParams.isbn;
            vm.getBookInfo();
        }

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function addBook(title, description, language, cover, authors){
            vm.book.title = title;
            vm.book.description = description.substring(0, 252);
            if(vm.book.description.length === 252) vm.book.description = vm.book.description + "...";
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
            vm.book.cover = cover;
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
