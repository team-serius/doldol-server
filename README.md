# 모이면 완성되는 마음의 종이, 돌돌

## 🌟 서비스 소개
'돌돌'은 소중한 사람들과 함께 마음을 주고받는 온라인 롤링페이퍼 서비스입니다. 직접 만나기 어려운 상황에서도 따뜻한 메시지를 주고받으며 서로의 소중한 순간을 함께할 수 있도록 기획되었습니다. 생일, 졸업, 기념일 등 특별한 날, 친구, 가족, 동료들에게 진심을 담은 롤링페이퍼를 선물해 보세요!


## ✨ 주요 기능
- 간편한 롤링페이퍼 생성: 몇 번의 클릭만으로 나만의 롤링페이퍼를 만들 수 있습니다.
- 롤링페이퍼 공유: 원하는 사람들에게 링크를 공유하여 롤링페이퍼에 초대해보세요!
- 메시지 공개 날짜 설정 : 특정 날짜 이전까지는 메시지가 보이지 않도록 설정할 수 있습니다. 이를 통해 미래에 보내는 편지를 보내보세요.

## 🚀 프로젝트 개요 및 개발 과정
'돌돌 서버' 프로젝트는 개발 동아리 시리우스에서 4명의 백엔드 개발자가 협력하여 진행되었습니다.

</br>

## 💻 BE 팀원 소개

<table>
  <tr>
    <td align="center"><strong>개발자</strong></td>
    <td align="center"><strong>담당 기능</strong></td>
  </tr>
  <tr>
    <td align="center">
      <a href="https://github.com/ehddbs4521">
        <img width="150" height="150" src="https://github.com/ehddbs4521.png" alt="김동윤"/>
        <br/><strong>김동윤</strong><br/>
        <small>(BE-PO)</small>
      </a>
    </td>
    <td align="left">
      • API 명세서 작성<br/>
      • ERD 구성<br/>
      • CI/CD 파이프라인 구축<br/>
      • 무중단 배포<br/>
      • 인증/인가 (소셜 로그인 유저와 자체 서비스 로그인 유저를 통합 관리)<br/>
      • 메시지 CRUD
    </td>
  </tr>
  <tr>
    <td align="center">
      <a href="https://github.com/cherryiJuice">
        <img width="150" height="150" src="https://github.com/cherryiJuice.png" alt="최이주"/>
        <br/><strong>최이주</strong><br/>
        <small>(BE-PM)</small>
      </a>
    </td>
    <td align="left">
      • 담당 역할 추가 예정
    </td>
  </tr>
  <tr>
    <td align="center">
      <a href="https://github.com/hoonssac">
        <img width="150" height="150" src="https://github.com/hoonssac.png" alt="서상훈"/>
        <br/><strong>서상훈</strong><br/>
        <small>(BE-Member)</small>
      </a>
    </td>
    <td align="left">
      • 담당 역할 추가 예정
    </td>
  </tr>
  <tr>
    <td align="center">
      <a href="https://github.com/JungwooMoon">
        <img width="150" height="150" src="https://github.com/JungwooMoon.png" alt="문정우"/>
        <br/><strong>문정우</strong><br/>
        <small>(BE-Member)</small>
      </a>
    </td>
    <td align="left">
      • API 명세서 작성 <br/>
      • Spring 초기 설정 <br/>
      • 이슈 및 PR 템플릿 작성 <br/>
      • 유저 정보 조회 API <br/>
    </td>
  </tr>
</table>

</br>

## 🛠️ Tech Stack
![Frame 1](https://github.com/user-attachments/assets/7b88bf64-4c33-4a6e-b799-4c2713674dd4)
![Frame 2](https://github.com/user-attachments/assets/b5e78afb-4ba9-4091-acbb-514936f114e1)

</br>

## 🛠️ ERD
<img width="1336" height="544" alt="image" src="https://github.com/user-attachments/assets/f36fa3cf-2780-4c79-86f3-0d97cd70a0e2" />

</br>

## 🛠️ API 명세서
**[Swagger UI에서 API 명세서 확인하기](https://api-doldol.wha1eson.co.kr/swagger-ui/index.html)**

</br>

## 🛠️ CI/CD PipeLine
<img width="1321" height="416" alt="be-ci:cd" src="https://github.com/user-attachments/assets/a6117913-03ee-434d-ac63-246c323d40d9" />

</br>

## 🛠️ BE Infra
<img width="886" height="547" alt="be-architecture" src="https://github.com/user-attachments/assets/9594adb0-7441-4056-9082-876ff3d39b2d" />

</br>

## 👨‍💻 개발자별 고민사항 및 해결과정

<table>
 <tr>
   <th width="20%">👤 개발자</th>
   <th width="35%">🤔 고민사항</th>
   <th width="45%">💡 해결과정</th>
 </tr>
 
 <tr>
   <td rowspan="4" width="20%" align="center">
     <img width="80" height="80" src="https://github.com/ehddbs4521.png" alt="김동윤"/><br/>
     <strong><a href="https://github.com/ehddbs4521">김동윤</a></strong><br/>
     <small>(BE-PO)</small>
   </td>
   <td><strong>Q1. 백엔드 서버를 Public Subnet에 위치시킨 이유</strong></td>
   <td>
     보안 측면에서는 Private Subnet에 RDS와 EC2를 배치하는 것이 더 안전합니다. 하지만 소셜 로그인 구현 시 Private Subnet의 EC2에서 외부 OAuth 서버로 API 요청을 보내기 위해서는 NAT Gateway 또는 NAT Instance와 같은 추가 인프라가 필요합니다.<br/><br/>
     초기 개발 단계에서 인프라 복잡도와 비용을 고려하여 Public Subnet을 선택했으며, 대신 Security Group을 통해 보안을 강화했습니다.<br/><br/>
     <a href="https://velog.io/@rlaehddbs4521/Infra-CI-CD-%EC%84%B8%ED%8C%85">📝 참고 블로그</a>
   </td>
 </tr>
 
 <tr>
   <td><strong>Q2. CI/CD 툴 선택 이유</strong></td>
   <td>
     t2.micro EC2 인스턴스는 메모리 용량이 제한적이어서 Jenkins를 Docker로 구동할 경우 메모리 스왑이 발생할 수밖에 없었습니다. 메모리 스왑은 성능 저하를 유발할 수 있어 지양했습니다.<br/><br/>
     GitHub Actions는 프라이빗 레포지토리에 대해 월 500MB, 2,000분의 무료 사용량을 제공하며, 파이프라인 구성이 Jenkins에 비해 간단하고 직관적이어서 생산성을 높일 수 있다는 점에서 선택했습니다.<br/><br/>
     <a href="https://velog.io/@rlaehddbs4521/Infra-CI-CD-%EC%84%B8%ED%8C%85">📝 참고 블로그</a>
   </td>
 </tr>
 
 <tr>
   <td><strong>Q3. CI와 CD 워크플로우 구성 전략</strong></td>
   <td>
     무중단 배포(Blue-Green Deployment)를 도입했기 때문에, 배포 시 안정성과 신중함이 중요했습니다. 따라서 CD 과정은 자동이 아닌 수동 트리거 방식으로 구성하였고, latest 버전과 롤백 시 사용할 이전 버전을 명시적으로 입력받아 배포하도록 설계했습니다.<br/><br/>
     이를 통해 배포 시 발생할 수 있는 리스크를 최소화하고, 빠르게 복구할 수 있는 구조를 갖추었습니다.
   </td>
 </tr>
 
 <tr>
   <td><strong>Q4. 데이터 암호화 범위 결정</strong></td>
   <td>
     데이터는 민감도와 사용 목적에 따라 선택적으로 암호화하는 것이 중요하다고 생각합니다. 비밀번호는 해싱 방식으로 처리하고, 메시지처럼 다시 보여줘야 하는 정보는 복호화 가능한 암호화를 적용했습니다.<br/><br/>
     고정 Salt를 사용한 암호화는 보안에 취약하며, 복호화 과정에서 페이지 렌더링 지연과 서버 CPU 과부하가 발생할 수 있어 메시지 내용과 비밀번호에만 암호화를 적용했습니다.<br/><br/>
     <a href="https://velog.io/@rlaehddbs4521/Spring-%EC%8B%9C%EB%A6%AC%EC%9A%B0%EC%8A%A4-4%ED%8E%B8-%EC%95%94%ED%98%B8%ED%99%94-Secret">📝 참고 블로그</a>
   </td>
 </tr>
</table>
