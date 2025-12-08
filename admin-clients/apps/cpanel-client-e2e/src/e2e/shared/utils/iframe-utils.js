// export const getRichTextIframeBody = (formcontrolname) => {
// 	return cy.get(`[formcontrolname="${formcontrolname}"] iframe`)
// 		.its('0.contentDocument').should('exist')
// 		.find('body')
// 		.then(cy.wrap);
// }
const getIframeDocument = (formcontrolname) => {
	return cy
	.get(`[formcontrolname="${formcontrolname}"] iframe`)
	.its('0.contentDocument').should('exist')
}
  
export const getRichTextIframeBody = formcontrolname => {
	return getIframeDocument(formcontrolname)
	.its('body').should('not.be.undefined')
	.then(cy.wrap)
}