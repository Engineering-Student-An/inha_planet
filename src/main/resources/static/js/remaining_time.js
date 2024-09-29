function updateRemainingTime() {
    var remainingTimeElements = document.querySelectorAll('.remainingTime'); // 모든 남은 시간 요소 선택

    remainingTimeElements.forEach(function(remainingTimeElement) {
        var remainingSeconds = parseInt(remainingTimeElement.dataset.remainingSeconds);

        if (remainingSeconds > 0) {
            remainingSeconds--;

            var days = Math.floor(remainingSeconds / 86400); // 1일 = 86400초
            var hours = Math.floor((remainingSeconds % 86400) / 3600); // 1시간 = 3600초
            var minutes = Math.floor((remainingSeconds % 3600) / 60); // 1분 = 60초
            var seconds = remainingSeconds % 60; // 남은 초

            var timeString = '<span class="material-symbols-outlined">timer</span> D-';
            if (days > 0) {
                timeString += days + "일 ";
            }
            if (hours > 0) {
                timeString += hours + "시간 ";
            }
            timeString += minutes + "분 " + seconds + "초";

            remainingTimeElement.innerHTML = timeString; // 최종 문자열 설정
            remainingTimeElement.dataset.remainingSeconds = remainingSeconds; // 업데이트된 값 저장

            // 색상 변경 로직
            if (days <= 0 && hours <= 0) {
                remainingTimeElement.style.color = "purple"; // 만료된 경우
            } else if (days < 1) {
                remainingTimeElement.style.color = "red"; // 1일 이하
            } else if (days <= 3) {
                remainingTimeElement.style.color = "darkorange"; // 3일 이하 (주황색보다 진한 색)
            } else {
                remainingTimeElement.style.color = ""; // 기본 색상
            }

        } else {
            remainingTimeElement.textContent = "마감기한이 만료되었습니다!";
            remainingTimeElement.dataset.remainingSeconds = 0; // 더 이상 업데이트하지 않도록 설정
            remainingTimeElement.style.color = "purple"; // 만료된 경우 색상 설정
        }
    });
}

window.onload = function() {
    updateRemainingTime(); // 페이지 로드 시 즉시 업데이트
    setInterval(updateRemainingTime, 1000); // 1초마다 업데이트
};