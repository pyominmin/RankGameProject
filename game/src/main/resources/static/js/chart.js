$(document).ready(function() {
    // Fetch top 10 games data from the server
    $.get('/api/games/top10', function(data) {
        const labels = data.map(game => game.gameName);
        const votes = data.map(game => game.gameVote);

        // Create Top 10 Games Chart
        var ctx = document.getElementById('gameRankChart').getContext('2d');
        var gameRankChart = new Chart(ctx, {
            type: 'bar', // 가로형 바 그래프로 설정
            data: {
                labels: labels,
                datasets: [{
                    label: '득표수',
                    data: votes,
                    backgroundColor: 'rgba(75, 192, 192, 0.2)',
                    borderColor: 'rgba(241, 134, 255)',
                    borderWidth: 1
                }]
            },
            options: {
                indexAxis: 'y', // 가로형 바 그래프로 설정
                scales: {
                    x: {
                        beginAtZero: true,
                        ticks: {
                            color: 'white' // x축 글씨 색상 변경
                        }
                    },
                    y: {
                        ticks: {
                            color: 'white' // y축 글씨 색상 변경
                        }
                    }
                },
                plugins: {
                    legend: {
                        labels: {
                            color: 'white' // 범례 글씨 색상 변경
                        }
                    }
                }
            }
        });
    });

    // Vote Trends by Game Chart - Rank
    var ctx4 = document.getElementById('vote-trend-chart').getContext('2d');
    var voteTrendChart = new Chart(ctx4, {
        type: 'line',
        data: {
            labels: [],
            datasets: [{
                label: '득표수',
                data: [],
                backgroundColor: 'rgba(75, 192, 192, 0.2)',
                borderColor: 'rgba(241, 134, 255)',
                borderWidth: 1
            }]
        },
        options: {
            scales: {
                y: {
                    beginAtZero: true,
                    reverse: true,  // 순위가 낮은 숫자가 상단에 표시되도록 반전
                    ticks: {
                        color: 'white' // y축 글씨 색상 변경
                    }
                },
                x: {
                    ticks: {
                        color: 'white' // x축 글씨 색상 변경
                    }
                }
            },
            plugins: {
                legend: {
                    labels: {
                        color: 'white' // 범례 글씨 색상 변경
                    }
                }
            }
        }
    });

    $("#game-select").change(function() {
        const gameId = $(this).val();
        if (gameId) {
            $.get(`/game/${gameId}/vote-trend`, function(data) {
                const labels = data.map(item => item.voteTime);
                const ranks = data.map(item => item.gameVote);

                voteTrendChart.data.labels = labels;
                voteTrendChart.data.datasets[0].data = ranks;
                voteTrendChart.update();
            });
        }
    });
});