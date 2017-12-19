package airwallex.payment.model

case class PaymentRequest(request_id : String,
                          source_currency: String,
                          payment_currency: String,
                          source_amount: String,
                          payment_amount: String,
                          fee_paid_by: String,
                          payment_method: String,
                          client_data: String,
                          payment_date: String,
                          reason: String,
                          reference: String,
                          payer: Payer,
                          beneficiary: Beneficiary)


case class Payer( entity_type: String,
                  company_name: String,
                  first_name: String,
                  last_name: String,
                  date_of_birth: String,
                  address: PayerAddress,
                  additional_info: PayerAdditionalInfo)

case class PayerAddress( country_code: String,
                         full_address: String,
                         state: String,
                         city: String,
                         street_address: String,
                         postcode: String)


case class PayerAdditionalInfo( business_registration_type: String,
                                business_registration_number: String,
                                business_area: String,
                                business_phone_number: String,
                                business_registration_expiry_date: String,

                                personal_first_name_in_chinese: String,
                                personal_last_name_in_chinese: String,
                                personal_nationality: String,
                                personal_id_type: String,
                                personal_id_number: String,
                                personal_id_expiry_date: String,
                                personal_occupation: String,
                                personal_mobile_number: String,
                                personal_email: String,

                                legal_rep_first_name: String,
                                legal_rep_last_name: String,
                                legal_nationality: String,
                                legal_rep_first_name_in_chinese: String,
                                legal_rep_last_name_in_chinese: String,
                                legal_rep_id_type: String,
                                legal_rep_id_number: String,
                                legal_rep_id_expiry_date: String,
                                legal_rep_occupation: String,
                                legal_rep_mobile_number: String,
                                legal_rep_email: String,
                                legal_rep_date_of_birth: String,
                                legal_rep_address: LegalRepAddress)


case class Beneficiary( entity_type: String,
                        company_name: String,
                        first_name: String,
                        last_name: String,
                        date_of_birth: String,
                        address: BeneficiaryAddress,
                        bank_details: BankAccount,
                        additional_info: BeneficiaryAdditionalInfo)


case class BeneficiaryAddress( country_code: String,
                               full_address: String,
                               state: String,
                               city: String,
                               street_address: String,
                               postcode: String)


case class BankAccount( account_name: String,
                        bank_country_code: String,
                        bank_name: String,
                        account_currency: String,
                        account_number: String,
                        account_routing_type1: String,
                        account_routing_type2: String,
                        account_routing_value1: String,
                        account_routing_value2: String,
                        bank_branch: String,
                        bank_street_address: String,
                        binding_mobile_number: String,
                        iban: String,
                        swift_code: String,
                        payment_method: String,
                        status: String)


case class BeneficiaryAdditionalInfo( business_registration_type: String,
                                business_registration_number: String,
                                business_area: String,
                                business_phone_number: String,
                                business_registration_expiry_date: String,

                                personal_first_name_in_chinese: String,
                                personal_last_name_in_chinese: String,
                                personal_nationality: String,
                                personal_id_type: String,
                                personal_id_number: String,
                                personal_id_expiry_date: String,
                                personal_occupation: String,
                                personal_mobile_number: String,
                                personal_email: String,

                                legal_rep_first_name: String,
                                legal_rep_last_name: String,
                                legal_nationality: String,
                                legal_rep_first_name_in_chinese: String,
                                legal_rep_last_name_in_chinese: String,
                                legal_rep_id_type: String,
                                legal_rep_id_number: String,
                                legal_rep_id_expiry_date: String,
                                legal_rep_occupation: String,
                                legal_rep_mobile_number: String,
                                legal_rep_email: String,
                                legal_rep_date_of_birth: String,
                                legal_rep_address: LegalRepAddress)

case class LegalRepAddress( country_code: String,
                         full_address: String,
                         state: String,
                         city: String,
                         street_address: String,
                         postcode: String)