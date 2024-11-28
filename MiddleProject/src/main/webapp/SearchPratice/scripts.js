//$(document).ready(function() {
//    $('#search-form').submit(function(event) {
//        event.preventDefault();
//        var searchQuery = $('#search-input').val().trim().toLowerCase();
//        
//        if (searchQuery === '') {
//            alert('여행지 이름을 입력하세요!');
//            return;
//        }
//
//        $.ajax({
//            url: 'travels.json',
//            method: 'GET',
//            dataType: 'json',
//            success: function(data) {
//                var results = data.filter(function(item) {
//                    return item.name.toLowerCase().includes(searchQuery);
//                });
//
//                displayResults(results);
//            },
//            error: function(xhr, status, error) {
//                console.error('Error fetching travel data:', error);
//            }
//        });
//    });
//
//    function displayResults(results) {
//        var resultsContainer = $('#results');
//        resultsContainer.empty();
//
//        if (results.length === 0) {
//            resultsContainer.append('<p>검색 결과가 없습니다.</p>');
//            return;
//        }
//
//        results.forEach(function(item) {
//            var resultItem = $('<div class="result-item"></div>');
//            resultItem.append('<h3>' + item.name + '</h3>');
//            resultItem.append('<p>' + item.location + '</p>');
//            resultItem.append('<p>' + item.description + '</p>');
//            resultItem.append('<a href="' + item.url + '" target="_blank">자세히 보기</a>');
//            resultsContainer.append(resultItem);
//        });
//    }
//});

// 검색하면 바로 하이퍼링크로 가는 코드 
$(document).ready(function() {
    $('#search-form').submit(function(event) {
        event.preventDefault();
        var searchQuery = $('#search-input').val().trim().toLowerCase();
        
        if (searchQuery === '') {
            alert('여행지 이름을 입력하세요!');
            return;
        }

        $.ajax({
            url: 'travels.json',
            method: 'GET',
            dataType: 'json',
            success: function(data) {
                var results = data.filter(function(item) {
                    return item.name.toLowerCase().includes(searchQuery);
                });

                if (results.length > 0) {
                    // 첫 번째 결과의 URL로 이동
                    window.location.href = results[0].url;
                } else {
                    alert('검색 결과가 없습니다.');
                }
            },
            error: function(xhr, status, error) {
                console.error('Error fetching travel data:', error);
            }
        });
    });
});


