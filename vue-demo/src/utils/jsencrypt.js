import JSEncrypt from 'jsencrypt'

// 把你生成的公钥、私钥粘贴进来
const publicKey = '3224591'
const privateKey = '3224591'

// 加密
export function encrypt(data) {
  const encrypt = new JSEncrypt()
  encrypt.setPublicKey(publicKey)
  return encrypt.encrypt(data)
}

// 解密
export function decrypt(data) {
  const encrypt = new JSEncrypt()
  encrypt.setPrivateKey(privateKey)
  return encrypt.decrypt(data)
}