# vote_scheduler.py

import MySQLdb
import random

# 데이터베이스 연결 설정
conn = MySQLdb.connect(
    host='localhost',
    user='root',
    password='12345',
    database='gamelist'
)

cursor = conn.cursor()

# 'votes' 열이 있는지 확인하고, 없으면 추가 및 기본값 설정
try:
    cursor.execute("SELECT votes FROM games LIMIT 1;")
except MySQLdb.ProgrammingError:
    cursor.execute("ALTER TABLE games ADD COLUMN votes INT DEFAULT 0;")
    conn.commit()

# 'votes' 열의 기본값을 0으로 설정
cursor.execute("ALTER TABLE games MODIFY COLUMN votes INT DEFAULT 0;")
conn.commit()

# add_dummy_votes 함수 정의
def add_dummy_votes():
    # 모든 게임의 ID 가져오기
    cursor.execute("SELECT id FROM games;")
    game_ids = cursor.fetchall()

    if not game_ids:
        print("No games found in the database.")
    else:
        # 각 게임에 대해 10000명의 더미 투표 추가
        for _ in range(10000):
            random_game_id = random.choice(game_ids)[0]
            cursor.execute("UPDATE games SET votes = votes + 1 WHERE id = %s;", (random_game_id,))
            conn.commit()

        print("Dummy votes added for all games.")

        # 각 게임의 이름과 현재 투표 수 출력
        cursor.execute("SELECT name, votes FROM games;")
        games = cursor.fetchall()
        for game in games:
            print(f"Game '{game[0]}' has {game[1]} votes.")

# 커서 및 연결 닫기
cursor.close()
conn.close()
