# **Chapter05 - 프로세스 동기화**

# **[ 📋 목차 ]**
- 프로세스 간 통신
- 공유 자원과 임계구역
- 임계구역 해결 방법
- 파일, 파이프, 소켓 프로그래밍

****

# **[ 🗂️ 정리 ]**
### 📌 프로세스 간 통신
#### ⚙︎ 개념
프로세스 간 통신(Inter-Process Communication, IPC) 는 컴퓨터 내의 프로세스 뿐 아니라 네트워크로 연결된 다른 컴퓨터의
프로세스도 포함된다.  

OS 에 도움 없이 진행되는 통신 방식에는 전역 변수 혹은 파일을 이용하는 방식이 있고, 반대로 OS 의 도움이 필요한 파이프, 소켓, 원격 프로시저 호출
(Remote Procedure Call, RPC) 가 있다.

#### ⚙ 통신 분류
- 양방향 통신(Duplex Communication)
  - 양쪽 방향에서 데이터 송, 수신이 가능한 구조 ex) 소켓
- 반양방향 통신(Half-Duplex Communication)
  - 동시에 양쪽에서 송신이 불가능한 구조 ex) 무전기
- 단방향 통신(Simplex Communication)
  - 한쪽으로만 송신 가능한 구조 ex) 전역 변수, 파이프
  
#### ⚙︎ 구현 방식에 따른 오류
전역 변수를 이용한 구현은 언제 데이터가 송신될지 모르기 때문에 지속적으로 확인을 해줘야 하는데 이 것을 Busy Waiting 이라고 한다.  
Busy Waiting 을 해결하기 위해 데이터 송신이 끝나면 알려주는 동기화(Synchronization) 을 사용해야 한다. (일종의 알림)  
이런 동기화 기능 유무에 따라사서 통신이 나뉘게 된다.

- Blocking Communication
- Non-Blocking Communication
- Synchronous Communication
- Asynchronous Communication

#### ⚙︎ 전역 변수를 이용한 통신
자식 프로세스를 생성 할 때 부모 프로세스에서 전역 변수를 생성해 놓았다면 부모-자식 프로세스간의 통신이 가능하다.

```
int GV;

int main() {
  int pid;
  
  pid = fork();
  ...
}
```

전역 변수를 여러개 생성해서 부모-자식 간의 양방향 통신도 가능하다.

```
int GV_FROM_PARENT;
int GV_FROM_CHILD;

int main() {
  int pid;
  
  pid = fork();
  ...
}
```

하지만 전역 변수를 사용한다면 언제 쓰기(write) 작업이 이루어질지 모르기 때문에 계속해서 확인을 해줘야 한다.

#### ⚙ 파일을 이용한 통신
파일 입출력 코드는 open(), read(), write(), close() 로 구성되어 있다.

```
int main() {
  int fd;
  char buf[5]
  
  fd=open("com.text", 0_RDWR);    // 파일 유무 확인 & 쓰기 권한 확인(읽기만: 0_RDONLY)
  write(fd, "Test", 5);           // Test 라는 문자열 작성(마지막 null 포함)
  read(fd, buf, 5);               // 5B 버퍼에 저장
  close(fd);
  exit(0)
}
```

쓰기 -> 데이터가 저장   
읽기 -> 입출력 프로레스로부터 데이터 가져옴  
OS 입장에서는 저장장치의 데이터를 읽고 쓰는 것도 프로세스-입출력 프로세스 통신이다.  
파일을 이용하면 OS 레벨에서 관리하지 않기 때문에 동기화를 스스로 해야 하는데 부모 프로세스가 wait()를 호출해서 자식 프로세스의 작업이
끝날 때까지 기다렸다가 작업을 시작한다.

#### ⚙ 파이프를 이용한 통신
프로세스가 파이프에 읽기 연산을 수행하고, 데이터가 아직 쓰여지지 않았다면 그 상태로 대기한다.  
다른 프로세스가 해당 파이프에 쓰기 연산을 수행하면 그 때서야 대기 상태가 풀리면서 동기화가 이루어진다.

- 이름 없는 파이프(Anonymous Pipe)
  - 부모-자식 관계의 프로세스
  - 같은 부모를 갖는 자식 프로세스
- 이름 있는 파이프(Named Pipe): FIFO 라 불리는 특수 파일을 이용하여 관련 없는 프로세스간의 통신에 사용

#### ⚙ 소켓을 이용한 통신
여러 컴퓨터에 있는 프로세스 간 통신을 '네트워킹' 이라고 한다.  
다른 컴퓨터와 통신하기 위해서는 위치를 파악하고, 여러 프로세스 중에서 통신할 프로세스를 식별해야 한다.  
소켓에 쓰기 연산을 하면 데이터가 전송되고, 읽기 연산을 통해서 데이터를 받는다.  
소켓은 동기화를 지원하고, 양방향 통신을 위해서 단 하나의 소켓만 있으면 된다.

****

### 📌 공유 자원과 임계구역
- 공유 자원(Shared Resources): 여러 프로세스가 공동으로 이용하는 변수, 메모리, 파일 등

#### ⚙ 공유 자원의 접근
'누가', '언제' 자원에 접근해 데이터를 읽고 쓰는 순서에 따라서 결과가 달라지기 때문에 예상치 못한 문제 발생을 대비해야 한다.  
두 개 이상의 프로세스가 공유 자원을 병행적으로 읽거나 쓰는 상황을 '경쟁 조건(Race Condition)' 이라고 한다.

#### ⚙ 임계 구역(Critical Section)
멀티 스레딩 환경에서 여러 스레드나 프로세스가 동시에 실행될 때, 같은 자원에 접근할 때 발생되는 문제를 방지하기 위해 동시에 접근할 수 없도록
제한된 코드 영역

- 상호 배제(Mutual Exclusion): 한 프로세스가 임계구역에 들어가면 다른 프로세스는 임계구역에 들어갈 수 없다.
- 한정 대기(Bounded Waiting): 어떤 프로세스도 무한 대기(Infinite Postpone) 하지 않아야 한다.
- Progress Flexibility: 한 프로세스가 다른 프로세스의 진행을 방해해서는 안된다.

****

### 📌 임계구역 해결 방법
단순하게 잠금(lock)을 걸어서 해결할 수 있다.

#### ⚙ 상호 배제 문제

```
int lock = false;

while(lock == true);
lock = true;
--- 임계구역 ---
lock = false;
```

1. 초기 lock 세팅은 false 로 임계구역 사용 가능을 의미한다.
2. while 다른 프로세스에 의해 점유중이면 계속해서 대기한다.
3. lock 이 false 이면 true 로 변경해 다른 프로세스가 접근하지 못하도록 변경하고, 작업 한다.
4. 작업이 끝나면 lock 을 다시 false 로 변경한다.

- 문제점
1. 위 방법에서 문제가 있는데 프로세스가 while 은 빠져나왔지만 lock 을 걸지 못한 상태일 때(프로세스가 timeout 으로 준비 상태로 변경)
2. while 을 돌면서 Busy Waiting 상태로 대기

#### ⚙ 한정 대기 문제
```
int lock1 = false;
int lock2 = false;

[ process 1 ] 
while(lock2 == true);
--- 임계구역 ---
lock1 = false;

[ process 2 ]
while(lock1 == true);
--- 임계구역 ---
lock2 = false;
```

프로세스마다 lock 을 따로 두어서 상호배제 문제를 해결 했지만 다른 문제가 있다.

- 문제점
1. 두 프로세스가 lock 을 true 로 설정하고 Timeout 으로 준비 상태로 들어가서 무한 루프에 빠진다. (교착 상태, Deadlock)
2. 확장성 문제: 프로세스가 추가되면 lock 도 추가되어야 하고, if 문 조건도 추가 해야한다.

#### ⚙︎ 융통성 문제
```
int lock = 1;

[ process 1 ] 
while(lock2 == 2);
--- 임계구역 ---
lock1 = 2;

[ process 2 ]
while(lock1 == 1);
--- 임계구역 ---
lock2 = 1;
```

상호 배제와 한정 대기를 보장하는 코드이다.  
하지만 번갈아가며 실행되는 구조로 한 프로세스가 연달아 임계구역에 진입하고 싶어도 그럴 수 없다.  
다른 프로세스에 의해 방해받는 현상을 경직된 동기화(Lockstep Synchronization) 라고 한다.

#### ⚙︎ 세마포어(Semaphore)
임계구역에 진입할 수 있는 프로세스의 수를 제한하고, 프로세스가 진입하면 스위치 누르듯 사용 가능한 프로세스의 수를 하나 빼고, 사용이 끝나면
그 수를 하나 올리는 식으로 동작한다.  
정해놓은 수가 0이 되면 그 후에 진입한 프로세스는 대기 리스트에서 대기하기 때문에 Busy Waiting 상태가 아니며, 임계구역을 점유중인 프로세스가
사용이 끝나면 wakeup() 시켜준다.

#### ⚙︎ 모니터(Monitor)
모든 프로세스가 공유자원 사용시 세마포어 알고리즘을 사용한다면 따로따로 구현할 필요가 없기 때문에 공유 자원을 내부로 숨기고, 공유 자원에 접근하기 위한 인터페이스만 제공한다.

****

### 📌 파일, 파이프, 소켓
#### ⚙︎ 파일
파일에 저장된 데이터는 왼쪽처럼 보이지만 실제로는 한 줄로 저장되는데 이를 순차 파일(Sequential File) 이라고 하며, 순차 파일에 접근하는 방식을
순차적 접근(Sequential Access) 라고 한다.

```
Text
Computer  ->    T e x t \n C o m p u t e r \n B o o k \0
Book            ↑ 시작위치
```

파일을 여는 방식
- 읽기 전용(Read Only)
- 읽기/쓰기(Read/Write)
- 쓰기 전용(Write only)
- 생성(Create)

fd(File Descriptor), buf(Buffer), read size 가 인자로 들어갈 때  
예를 들어 read(fd, buf, 5) 실행 한다면 'Text' 가 버퍼에 들어갈 것이고, 그 후 close(fd) 로 파일을 닫는다.  
이렇게 파일을 통해서 통신하게 되면 파일 입출력 함수들을 이용해서 통신하게 된다.  

```
#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>

void main()
{
  int pid, fd;
  char buf[5];
  
  fd=open("com.txt", 0_RDWR);
  pid=fork();
  
  if(pid<0 || fd <0) exit(-1);
  
  else if(pid==0){
    write(fd, "Test", 5);
    close(fd);
    exit(0); }
  
  else { wait(0)
    lessk(fd, 0, SEEK_SET);
    read(fd, buf, 5);
    printf("%s", buf);
    close(fd);
    exit(0);
  }
}
```

fork()를 통해서 생성된 자식 프로세스에게 부모가 open() 했던 파일에 대한 접근 권한까지 그대로 복사해준다.(= 부모&자식 둘 다 접근 가능)  
lseek() 를 통해서 위치를 재조정 하는 이유는 자식이 write() 작업을 한 직후 부모가 읽게 되면 fd가 변경되서 엉뚱한 위치에서 부터 읽게 된다.
  
중요한건 파일을 통한 프로세스 간 통신은 OS 레벨에서 동기화를 지원해주지 않기 때문에 wait() 같은 함수를 이용해서 동기화를 사용해야 한다.