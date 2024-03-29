# **Chapter04 - CPU  스케줄링**

# **[ 📋 목차 ]**
- 스케줄링의 개요
- 스케줄링 시 고려 사항
- 다중 큐
- 스케줄링 알고리즘
- 인터럽트 처리

****

# **[ 🗂️ 정리 ]**
### 📌 스케줄링의 개요
#### ⚙︎  스케줄링 단계
- 고수준 스케줄링(High Level Scheduling)
    - 다른 말로 잡 스케줄링(Job Scheduling)
    - 시스템 내의 전체 작업 '수' 를 조절하는 작업(= 프로세스의 개수)
    - OS 에서 다루는 가장 큰 작업의 단위로 1개 또는 여러개의 프로세스로 이루어진 작업을 말한다.
- 중간 수준 스케줄링(Middle Level Scheduling)
    - 시스템에서 활성화된 프로세스의 수를 조절하는 작업을 말한다.
        - 프로세스가 활성화된 상태여도 작업량에 따라서 과부하가 올 수 있기 때문에 필요하다.
    - 프로세스를 중지(Suspend) 혹은 활성화(Active)를 통해서 활성화된 프로세스의 '수' 를 조절한다.
- 저수준 스케줄링(Low Level Scheduling)
    - 상태에 관한 내용의 대부분은 저수준 스케줄링에 속한다.
    - 프로세스에게 CPU 를 할당하고, 상태를 변경하는 작업이다.

#### ⚙︎ 스케줄링의 목적
- 공평성: 특정 프로세스가 시스템 자원을 독점하여 다른 프로세스가 배제되지 않도록 한다.
- 효율성: 유후 시간(Idle Time)을 줄이기 위해서(= CPU 를 쉬지 않고 계속해서 일하게 만들기 위해)
- 안정성: 우선순위를 두어 중요 프로세스 먼저 자원 배정해 시스템 자원을 점유하거나 파과하지 못하게한다.
- 확장성: 프로세스가 늘어도 시스템이 안정적으로 작동 해야한다.
- 반응 시간 보장: 응답이 없는경우 시스템이 중지했다고 판단하기 때문에 프로세스 요구에 응답을 해야한다.
- 무한 연기 방지: 특정 프로세스의 작업이 무한히 연기되면 안된다.

```
[ 내 생각 ]
⚙︎ 무한 연기 -> 기아 상태(Starving State)를 말하는거 같음 우선순위가 낮게 잡힌
프로세스는 자원 할당을 받지 못해서 무한히 연기되는 케이스

⚙︎ 안정성 -> 우선순위가 낮게 책정된 프로세스는 중요하지 않아서 시스템 자원에 영향을 끼칠 수 
있다는 건가? (이 부분은 잘 모르겠는데)

GPT -> 중요한 프로세스의 실행을 보장 시스템 자원 과부하 상태 방지로 불안정성 개선 가능하다는
```

****

### 📌 스케줄링 시 고려 사항
#### ⚙︎ 선점형, 비선점형
- 선점형(Preemptive): CPU 를 다른 프로세스가 점유하고 실행중에 있어도 CPU 를 강제로 뺏을 수 있는 스케줄링 방식
    - 대표적인 예시로 '인터럽트' 가 있다.
    - Context Switching 이 일어나기 때문에 '단점' 에 해당한다.
    - 하나의 프로세스가 독점하는게 아니라 '대화형 시스템' 혹은 '시분할 시스템' 에 적합하다.
- 비선점형(Non-preemptive): 다른 프로세스가 CPU 를 뺏을 수 없는 스케줄링 방식 
    - Context Switching 이 없는건 '장점' 에 해당한다.
    - 프로세스의 CPU 사용 시간이 길게 되면 다른 프로세스들이 오래 기다려서 '처리율' 이 떨어진다.

|구분|선점형|비선점형|
|:-:|------|:-----:|
|작업방식|작업 중단 -> 새로운 작업 실행|실행 상태의 작업의 완료까지 대기|
|장점|독점 방지로대화형, 시분할에 적합|CPU 작업량이 적고, 문맥 교환 오버헤드 적음|
|단점|문백 교환 오버헤드 발생|대기중인 프로세스는 처리율 감소|
|사용|시분할 방식 스케줄러|일괄 작업 방식 스케줄러|
|중요도|높음|낮음|

#### ⚙︎ 프로세스 우선순위
프로세스의 우선순위가 없다면 하나의 큐에서 준비 상태로 있는것과 마찬가지다.  
일반적으로 커널 프로세스가 일반 프로세스보다 높은 우선순위를 갖는데 만약 우선순위가 없다면 
커널과 관련된 중요한 프로세스의 작업이 뒤로 밀릴 수 있다.

#### ⚙ CPU 집중 프로세스와 입출력 집중 프로세스
프로세스가 실제로 CPU 를 사용하여 작업하는 상태는 '실행' 과 입출력 요청 완료를 기다리는 '대기' 상태가 있다.  
이 때 CPU 를 할당받아 실행하는 작업을 CPU Burst 와 IO Burst 라고 한다.
  
CPU 집중 프로세스 입출력 집중 프로세스가 같이 있을 때는 입출력 프로세스를 먼저 실행 상태로 보내는게 효율적이다.  
그 이유는 입출력 작업 동안 프로세스는 대기 상태로 옮겨지면서 다른 프로세스가 실행 될 수 있기 때문이다.  
이런 케이스를 사이클 훔치기(Cycle Stealing) 이라고 한다.

#### ⚙ 전면 프로세스, 후면 프로세스
- 전면 프로세스: GUI 를 사용하는 OS 에서 화면 맨 앞에 놓인 프로세스를 말하며 현재 I/O 사용중인 프로세스이다.
- 후면 프로세스: 사용자와 상호작용 없는 프로세스로 사용자의 입력 필요 없는 일괄 작업 프로세스이다.

****

### 📌 다중 큐(Multiple Queue)
#### ⚙ 준비 상태의 다중 큐
하나의 자료 구조에서 모든 프로세스의 PCB 에 저장되어 있는 '우선순위' 를 확인하는 작업은 비효율적이다.  
우선순위별로 여러개의 큐에서 프로세스를 관리하는 방식이다.  

![KakaoTalk_Photo_2024-02-22-20-15-45 001jpeg](https://github.com/nashs789/Book-Study/assets/59809278/8a489877-c647-4588-b10b-33fe709429b2)
  
큐의 개수나 프로세스 선정은 스케줄링 알고리즘에 따라 달라진다.
우선순위 배정에는 2가지 방식이 있다.

- 고정 우선순위 방식(Static Priority)
  - 한 번 우선순위 부여시 프로세스 끝날 때까지 변경 없다.
  - 구현은 쉽지만 시스템의 변화에 대응하기 힘들다.
- 변동 우선순위 방식(Dynamic Priority)
  - 우선순위가 작업 중간에 변하는 방식
  - 구현은 어렵지만 시스템 효율성이 높다.
  
낮은 우선순위의 프로세스가 더 빠르게 실행되야 해서 우선순위가 높도록 변경하는것을 Priority Inversion 이라고 한다.

#### ⚙ 대기 상태의 다중 큐
입출력을 대기중인 상태에서도 다중 큐를 이용해서 관리한다.  
다양한 입출력장치가 있기 때문에 여러개의 다중 큐가 필요하다.  
  
여러 입출력 장치가 동시에 작업을 끝내고 인터럽트를 발생 시키기도 하는데, 인터럽트 벡터(Interrupt Vector) 자료 구조를 사용해서 처리한다.  

![KakaoTalk_Photo_2024-02-22-20-15-46 002jpeg](https://github.com/nashs789/Book-Study/assets/59809278/5edc559e-910d-41cf-9fc4-5e7f5b94a777)

****

### 📌 스케줄링 알고리즘

|구분|종루|
|:-:|------|
|비선점형 알고리즘|FCFS, SJF, HRN|
|선점형 알고리즘|라운드 로빈, SRT, MLQ, MLFQ|
|둘 다 가능|우선순위|

#### ⚙ 스케줄링 알고리즘 선택 기준
- CPU 사용률: 전체 시스템 동작 시간 중 CPU 가 사용된 시간
- 처리량: 단위 시간당 작업을 마친 프로세스의 수(클 수록 좋다.)
- 대기 시간: 프로세스가 작업을 시작하기 전까지는 대기 시간으로 측정한다.(짧을 수록 좋다.)
- 응답 시간: 사용자 요구에 얼마나 빨리 반응하는지 측정으로 요청에 따른 첫 응답 시간 측정(짧을 수록 좋다.)
- 반환 시간: 프로세스가 종료되어 사용하던 잔원 반환까지 걸리는 시간(대기 시간 + 실행 시)

#### ⚙ First Come First Served, FCFS (= FIFO)
하나의 큐에서 도착한 순서대로 실행되며, 모든 프로세스는 동일한 우선순위를 갖고, 하나의 프로세스가 끝나야 다른 프로세스가 생행될 수 있다.

![KakaoTalk_Photo_2024-02-22-20-15-46 003jpeg](https://github.com/nashs789/Book-Study/assets/59809278/212483d5-302a-4996-832e-5f93932f8014)

구현이 단순하고 프로세스들에게 공정한 자원 배분을 하지만, 처리시간이 긴 프로세스를 먼저 처리하게 되면 콘보이 효과(Convoy Effect) 라고,  
컨베이어 벨트에 작업물이 한 줄로 늘어질 때 앞에서 지연되면 뒤까지 지연되는 현상을 겪는다.  
또한 입출력 작업까지 요청하게 된다면 프로세스 종료 시점이 더 미뤄져서 CPU 사용률도 떨어지게 된다.

#### ⚙ Shortest Job First, SJF
단어 그래도 짧은 작업을 갖는 프로세스 먼저 CPU를 할당해주는 알고리즘이다.

![KakaoTalk_Photo_2024-02-22-20-15-46 004jpeg](https://github.com/nashs789/Book-Study/assets/59809278/34d506ce-d9a3-4c51-a97d-16f69480035a)

가장 큰 문제는 정확한 종료 시간을 예측할 수 없다는 것이다.  
또 사용자와의 상호작용도 있다면, 예측은 더욱 더 힘들어지기 때문이다.  
프로세스가 자신의 시간을 측정해 OS에 알려주는 방식도 있지만 악의적인 프로세스를 배제할 수 없다.
  
다른 문제로는 작업 시간이 크게 측정된 프로세스는 CPU 할당을 받지 못하는 상황이 생길 수 있다.  
이 현상을 아사(Starving) 현상 이라고 한다.(= Infinite Blocking)  
나이(Aging) 처리를 통해서 아사 현상을 처리할 수 있지만 기준이 명확하지 않다.

#### ⚙ Highest Response Ratio Next, HRN
대기 시간과 CPU 사용 시간을 고려하여 우선순위를 측정하는 알고리즘으로 SJF의 아사 현상을 해결하기 위한 Aging 구현이 되어 있다고 볼 수 있다.  

```
우선순위 = 대기시간 + CPU 사용 시간 / CPU 사용 시간 
```

하지만 충분히 공평한 알고리즘은 아니기 때문에 많이 쓰이지 않는다.

#### ⚙ Round Robin, RR
할당받은 타임 슬라이스만큼 작업 후 작업을 끝내지 못했다면 준비 큐 맨 뒤로 돌아가는 방식이다.  
우선순위가 없으며 선점형 알고리즘 중에서는 가장 구현이 단순하다.  

![KakaoTalk_Photo_2024-02-22-20-15-46 005jpeg](https://github.com/nashs789/Book-Study/assets/59809278/c4d7fcf8-7956-4ea3-87f8-2647a8cb3eec)

FCFS 보다 무작정 좋다고는 단정할 수 프로세스 작업 순서에 따라서 평균 대기 시간이 달라지기 때문이다.  
그리고 FCFS와 대기 시간이 같다면 라운드 로빈은 문맥 교환에 들어가는 시간이 있기 때문에 더 비효율적인 알고리즘이다.  
  
타임 슬라이스의 크기에 따라서도 알고리즘은 영향을 받는데
- 타임 슬라이스가 큰 경우
  - 타임 슬라이스가 무한대인 경우 FCFS와 같다.
  - 여러 프로세스가 동시에 실행되고 있는 경우에는 프로세스가 끊겨서 보일 것이다.
- 타임 슬라이스가 작은 경우
  - 여러 프로세스가 동시에 실행되는 것처럼 보일 것이다.
  - 잦은 문맥 교환으로 시스템의 전반적인 영향을 미친다.

#### ⚙ Shortest Remaining Time, SRT
SJF 와 RR 알고리즘이 혼합된 알고리즘 이다.  
기본 베이스는 RR 알고리즘 이지만 다음 프로세스를 선정할 때 남은 작업 시간이 가장 적은 프로세스를 선택한다.

![KakaoTalk_Photo_2024-02-22-20-15-46 006jpeg](https://github.com/nashs789/Book-Study/assets/59809278/a1d6099f-b14d-4b2d-923a-68773d4262e4)

좋은 알고리즘은 아닌게 현재 실행 중인 프로세스와 큐에 남아있는 프로세스의 남은 시간을 주기적으로 계산해야 하고, 더 적은 프로세스가 있다면  
문맥 교환을 통해서 스케줄링을 진행 해야한다.   
아사 현상의 가능성도 있고, OS가 프로세스의 종료 시간을 예측 하는건 어려운 일이다.

#### ⚙ Priority
선점형과 비선점형 알고리즘에서 모두 사용 가능하다.  
우선순위가 고정되는 '고정' 우선순위 알고리즘과 일정 시간마다 새로 계산해서 변경하는 '변동' 우선순위 알고리즘이 존재한다.  
  
우선순위에 따라서 CPU를 배분하기 때문에 아사 현상의 가능성이 높고, 준비 큐의 프로세스의 우선순위를 변경해야 하니 오버헤드도 발생한다.  
그럼에도 시스템 효율성보다 프로세스 중요도가 우선되는 이유는 커널 프로세스와 일반 프로세스를 생각 해보면 쉽게 알 수 있다.

#### ⚙ Multilevel Queue, MLQ
우선순위에 따라서 라운드 로빈 알고리즘으로 운영되는 준비 큐를 여러개 사용하는 알고리즘이다.
우선순위에 따라서 타임 슬라이스를 조절해 효율성을 올릴 수 있다. (전면 프로세스는 크게 줘서 반응성을 높이고, 후면 프로세스는 FCFS 로 처리)  

![KakaoTalk_Photo_2024-02-22-20-15-46 007jpeg](https://github.com/nashs789/Book-Study/assets/59809278/dab5950f-2455-47e2-8220-1ad23519d973)

하지만 우선순위가 낮은 큐는 계속해서 기다려 작업이 연기되는 문제가 생길 수 있어 다음으로 제안되는게 MLFQ 이다.

#### ⚙ Multilevel Feedback Queue, MLFQ
MLQ와 같이 우선순위에 따른 여러 개의 큐를 사용한다.  
다른 점은 CPU를 사용한 프로세스의 우선순위가 낮아지는데, 프로세스의 우선순위를 조절해서 우선순위가 낮은 프로세스도 기회를 얻도록 한다.  
(커널 프로세스와 일반 프로세스의 차이는 둔다.)

![KakaoTalk_Photo_2024-02-22-20-15-46 008jpeg](https://github.com/nashs789/Book-Study/assets/59809278/d237200e-280f-4802-b7af-d6f0326b21cc)

또 우선순위가 낮은 프로세스는 CPU 를 할당 받는게 더 힘드니 더 오래 사용할 수 있도록 타임 슬라이스를 크게 잡아서 준다.  
MLFQ 의 마지막 큐는 타임 슬라이스를 무한대로 할당해 프로세스가 끝날 수 있도록 해주는데, 마지막 큐는 사실상 FCFS 라고 봐도 무방하다.  
  
오늘날 OS가 사용하는 CPU 스케줄링 방식이고, 변동 우선순위 알고리즘의 전형적인 예시이다.

****

### 📌 인터럽트 처리
#### ⚙︎ 개념
초기에는 위에서부터 한 줄씩 차례로 실행하는 순차적 프로그래밍(Sequential Programming) 이었지만, 특정 문제들을 해결하기에 부족했다.  
이를 해결하기 위해서 이벤트 드리븐(Event Driven) 방식이 나왔다.

> 브라우저 상단에 '최소화' '최대화' '종료' 버튼 3개가 있는데 순차적 프로그래밍은 주기적으로 버튼이 눌렸는지 확인 해줘야 한다. (아마도 polling 방식을 말하는 것 같음)
>   
> 이벤트 드리븐 방식은 버튼이 눌리면 프로세스에 알려주는 방식이다.︎

위와 같이 입출력 요청이 완료되면 이벤트를 발생 시켜서 이 사실을 알리는데 이를 '인터럽트'라고 한다.

#### ⚙︎ 동기적, 비동기적 인터럽트
- 동기적 인터럽트(Synchronous Interrupt): 실행중인 명령어로 발생
  - 프로그램상의 문제(다른 사용자의 메모리에 접근, 오버 플로우)
  - 의도적으로 발생 시키는 인터럽트 ex-macOS) cmd + c
  - 입출력 장치에 의한 인터럽트
  - 산술 연산중 발생하는 인터럽트(Divide by Zero)
- 비동기적 인터럽트(Asynchronous Interrupt): 실행중인 명령어와 무관하게 발생
  - 하드디스크 읽기 오류
  - 메모리 불량
  - 사용자가 사용하는 하드웨어 ex) 키보드, 마우스
  
#### ⚙︎ 인터럽트 처리 과정
시스템에는 많은 인터럽트가 존재하고 각각의 인터럽트에는 고유 번호(Interrupt Request, IRQ) 가 존재해 IRQ 를 통해서 인터럽트를 식별한다.
인터럽트는 한 번에 하나씩이 아니라 여러개가 발생할 수 있는데 동시에 발생하는 인터럽트를 하나로 묶어서 처리하는 개념이 '인터럽트 벡터'이다.

![KakaoTalk_Photo_2024-02-22-20-15-46 009jpeg](https://github.com/nashs789/Book-Study/assets/59809278/94f33a18-c3f4-4f68-a08f-c8dc05588cc6)

각 인터럽트 벡터에는 각 인터럽트를 처리하는 함수(인터럽트 핸들러, Interrupt Handler)가 1:1로 연결되어 있다.

[ 처리 과정 ]
1. 현재 실행 중인 프로세스는 일시 정지 상태가 되고, 나중에 재시작을 위해 프로세스 정보 저장
2. 인터럽트 컨트롤러가 실행되어 인터럽트 처리 순서를 결정한다. (우선순위에 따라)
3. 등록된 핸들러가 동작한다.
4. 처리를 마치고, 일시 정지된 프로세스를 다시 실행한다.(메모리 영역 침범같은 인터럽트가 발생했었다면 프로세스를 종료)
