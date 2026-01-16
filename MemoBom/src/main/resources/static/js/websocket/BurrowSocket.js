class ReceiveInfo {
	constructor(address, callback) {
		this.address = address;
		this.callback = callback;
		this.subscription = null;
	}
}

class ConnectionInfo {
	constructor(serverAddress, headers = {}) {
		this.serverAddress = serverAddress;
		this.headers = headers;
		this.onOpen = null;
		this.onClose = null;
	}

	setOpenHandler(handler) {
		this.onOpen = handler;
	}

	setCloseHandler(handler) {
		this.onClose = handler;
	}
}

class SocketScript {
	constructor(connectionInfo) {
		this.connectionInfo = connectionInfo;
		this.stompClient = null;
		this.connected = false;
		this.connecting = false;
		this.receiveInfos = [];
	}

	// ✅ 동적 추가용: 주소 + 콜백으로 바로 등록
	addReceive(address, callback) {
		const info = new ReceiveInfo(address, callback);
		this.receiveInfos.push(info);

		// 이미 연결되어 있다면 바로 subscribe
		if (this.connected && this.stompClient?.connected) {
			info.subscription = this.stompClient.subscribe(
				info.address,
				info.callback
			);
			console.log("Subscribed (dynamic) to", info.address);
		}
		return info;
	}

	// ✅ 기존 방식 유지하고 싶으면 이 메서드도 이렇게 수정
	addReceiveInfo(info) {
		this.receiveInfos.push(info);

		if (this.connected && this.stompClient?.connected) {
			info.subscription = this.stompClient.subscribe(
				info.address,
				info.callback
			);
			console.log("Subscribed (dynamic) to", info.address);
		}
	}

	// ✅ 주소나 ReceiveInfo로 구독 해제 가능하게
	unsubscribe(target) {
		const isAddress = typeof target === "string";

		for (let i = this.receiveInfos.length - 1; i >= 0; i--) {
			const info = this.receiveInfos[i];

			if ((isAddress && info.address === target) || (!isAddress && info === target)) {
				if (info.subscription) {
					try {
						info.subscription.unsubscribe();
						console.log("Unsubscribed from", info.address);
					} catch (e) {
						console.error("unsubscribe 중 에러:", e);
					}
					info.subscription = null;
				}
				this.receiveInfos.splice(i, 1);
			}
		}
	}

	connect() {
		console.log("연결 시도", this.connectionInfo.serverAddress);

		if (this.connected || this.connecting) {
			console.log("이미 연결 중이거나 연결됨");
			return true;
		}
		if (this.receiveInfos.length === 0) {
			console.warn("구독 정보가 없습니다.");
			// return false;
		}

		this.connecting = true;

		const socket = new SockJS(this.connectionInfo.serverAddress, null, {
			withCredentials: true, // 쿠키 쓸 경우
		});
		this.stompClient = Stomp.over(socket);
		// this.stompClient.debug = null; // 디버그 로그 끄고 싶으면

		this.stompClient.connect(
			this.connectionInfo.headers,
			(frame) => this._onConnect(frame),
			(error) => this._onError(error)
		);

		socket.onclose = () => this._onSocketClose();

		console.log("연결 시도 완료");
		return true;
	}

	disconnect() {
		if (!this.stompClient) {
			// 이미 끊긴 상태일 수도 있으니 그냥 상태만 정리하고 종료
			this.connected = false;
			this.connecting = false;
			return;
		}

		try {
			// 구독 해제
			this.receiveInfos.forEach((info) => {
				if (info.subscription) {
					try {
						info.subscription.unsubscribe();
					} catch (e) {
						console.error("unsubscribe 중 에러:", e);
					}
					info.subscription = null;
				}
			});

			// STOMP disconnect 호출
			this.stompClient.disconnect(() => {
				console.log("STOMP disconnected");
				this.connected = false;
				this.connecting = false;
				this.stompClient = null;
				if (this.connectionInfo.onClose) {
					this.connectionInfo.onClose();
				}
			});
		} catch (e) {
			console.error("disconnect 중 에러:", e);
			// 에러 나더라도 상태는 강제로 정리
			this.connected = false;
			this.connecting = false;
			this.stompClient = null;
			if (this.connectionInfo.onClose) {
				this.connectionInfo.onClose();
			}
		}
	}

	_onConnect(frame) {
		console.log("STOMP connected:", frame);
		this.connected = true;
		this.connecting = false;

		// ✅ 이미 가지고 있던 receiveInfos 전부 다시 subscribe
		this.receiveInfos.forEach((info) => {
			// 혹시 이전 subscription이 남아있다면 정리
			if (info.subscription) {
				try {
					info.subscription.unsubscribe();
				} catch (e) {
					console.error("중복 구독 정리 중 에러:", e);
				}
				info.subscription = null;
			}

			info.subscription = this.stompClient.subscribe(
				info.address,
				info.callback
			);
			console.log("Subscribed to", info.address);
		});

		if (this.connectionInfo.onOpen) this.connectionInfo.onOpen(frame);
	}

	_onError(error) {
		console.error("WebSocket error:", error);
		this.connected = false;
		this.connecting = false;
		if (this.connectionInfo.onClose) this.connectionInfo.onClose(error);
	}

	_onSocketClose() {
		console.log("소켓 onclose 발생");
		this.connected = false;
		this.connecting = false;
		this.stompClient = null;
		if (this.connectionInfo.onClose) this.connectionInfo.onClose();
	}

	sendMessage(address, data, headers = {}) {
		if (!this.stompClient?.connected) {
			console.warn("소켓이 연결되어 있지 않음");
			if (this.connectionInfo.onClose) this.connectionInfo.onClose();
			return false;
		}
		try {
			this.stompClient.send(address, headers, JSON.stringify(data));
			return true;
		} catch (e) {
			console.error("메시지 전송 실패:", e);
			return false;
		}
	}
}


/*

use example:)

window.addEventListener("beforeunload", () => {
	try {
		if (socketScript) {
			socketScript.disconnect();
		}
	} catch (e) {
		// unload 중에 에러 터져도 UX에 지장 없게 조용히 처리
		console.error("beforeunload disconnect 에러:", e);
	}
});

// 이 값을 backend로 전달하면 personalCallBackStatus 로 수신 가능.
let currentUserKey = null;

const connectionInfo = new ConnectionInfo('/MyCall/BackEvent', { service: 'BackOffice' });
const socketScript = new SocketScript(connectionInfo);

connectionInfo.setOpenHandler((frame) => {
	const userName = frame.headers['user-name'];
    currentUserKey = userName || null;

    if (!userName) {
        console.warn("user-name 헤더가 없습니다. frame:", frame);
    }

    console.log("연결됨", currentUserKey);
});
connectionInfo.setCloseHandler(() => {
	currentUserKey = null;
	console.log("연결 해제");
});

socketScript.addReceiveInfo(new ReceiveInfo('/user/toFront/ReceiveStatus', personalCallBackStatus));
socketScript.addReceiveInfo(new ReceiveInfo('/toFront/RecieveStatus', publicCallBackStatus));
socketScript.connect();

// 나만을 위한 콜백
function personalCallBackStatus(receiveData) {
		
	if (!receiveData.body) {
        console.warn("빈 메시지 수신");
        return;
    }

    try {
        const json = JSON.parse(receiveData.body);
        // TODO: json 처리
    } catch (e) {
        console.error("personalCallBackStatus JSON 파싱 실패:", e, receiveData.body);
    }
}

// 아무나...
function publicCallBackStatus(receiveData) {
	
	if (!receiveData.body) {
        console.warn("빈 메시지 수신");
        return;
    }

    try {
        const json = JSON.parse(receiveData.body);
        // TODO: json 처리
    } catch (e) {
        console.error("personalCallBackStatus JSON 파싱 실패:", e, receiveData.body);
    }
}

 */
